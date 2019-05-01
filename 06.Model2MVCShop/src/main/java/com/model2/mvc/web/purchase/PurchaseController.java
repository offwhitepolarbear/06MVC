package com.model2.mvc.web.purchase;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.purchase.PurchaseService;
import com.model2.mvc.service.user.UserService;

//==> ȸ������ Controller
@Controller
public class PurchaseController {
	
	///Field
	@Autowired
	@Qualifier("purchaseServiceImpl")
	private PurchaseService purchaseService;
	//setter Method ���� ����
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
		
	public PurchaseController(){
		System.out.println(this.getClass());
	}
	
	//==> classpath:config/common.properties  ,  classpath:config/commonservice.xml ���� �Ұ�
	//==> �Ʒ��� �ΰ��� �ּ��� Ǯ�� �ǹ̸� Ȯ�� �Ұ�
	@Value("#{commonProperties['pageUnit']}")
	//@Value("#{commonProperties['pageUnit'] ?: 3}")
	int pageUnit;
	
	@Value("#{commonProperties['pageSize']}")
	//@Value("#{commonProperties['pageSize'] ?: 2}")
	int pageSize;
	
	
	@RequestMapping("/addPurchaseView.do")
	public String addPurchaseView(@RequestParam("prodNo") String prodNo, Model model) throws Exception {

		System.out.println("/addPurchaseView.do");
		model.addAttribute("product", productService.getProduct(Integer.parseInt(prodNo)));
		
		return "forward:/purchase/addPurchaseView.jsp";
	}
	
	@RequestMapping("/addPurchase.do")
	public String addPurchase( @ModelAttribute("purchase") Purchase purchase, @ModelAttribute("user") User user, @ModelAttribute("product") Product product) throws Exception {

		System.out.println("/addPurchase.do");
		//Business Logic
		purchase.setBuyer(user);
		purchase.setPurchaseProd(product);
		purchaseService.addPurchase(purchase);
		
		return "redirect:/listPurchase.do";
	}
	
	@RequestMapping("/getPurchase.do")
	public String getPurchase( @RequestParam("tranNo") String tranNo , Model model ) throws Exception {
		
		System.out.println("/getUser.do");
		//Business Logic
		Purchase purchase = purchaseService.getPurchase(Integer.parseInt(tranNo));
		// Model �� View ����
		model.addAttribute("purchase", purchase);
		
		return "forward:/purchase/getPurchase.jsp";
	}
	
	@RequestMapping("/updatePurchaseView.do")
	public String updatePurchaseView( @RequestParam("tranNo") String tranNo , Model model ) throws Exception{

		System.out.println("/updateUserView.do");
		//Business Logic
		Purchase purchase = purchaseService.getPurchase(Integer.parseInt(tranNo));
		// Model �� View ����
		model.addAttribute("purchase", purchase);
		
		return "forward:/purchase/updatePurchaseView.jsp";
	}
	
	@RequestMapping("/updatePurchase.do")
	public String updatePurchase( @ModelAttribute("purchase") Purchase purchase) throws Exception{

		System.out.println("/updatePurchase.do");
		//Business Logic
		purchaseService.updatePurchase(purchase);
		
		return "redirect:/getPurchase.do?tranNo="+purchase.getTranNo();
	}
		
	@RequestMapping("/updateTranCode.do")
	public String updateTranCode(HttpSession session,@ModelAttribute("purchase") Purchase purchase, @RequestParam(value="prodNo", defaultValue="1") String prodNo) throws Exception{

		// �޴� �ȵ����� �� ó���� ��� �Ұ��ΰ�
		//����Ʈ��ü�̽����� �޴��� �ȳ���
		//����Ʈ���� �Ҵ��ؼ� ������ �����°� ����
		
		
		System.out.println("/updateTranCode.do");

			Product product = new Product();
			product.setProdNo(Integer.parseInt(prodNo));
			purchase.setPurchaseProd(product);
			purchaseService.updateTranCode(purchase);	
			User user = (User)session.getAttribute("user");
			if(user.getRole().equals("admin")){
			return "redirect:/listSalePurchase.do";
			}
			else {
				return "redirect:/listPurchase.do";
			}
				


		
	}
		
	@RequestMapping("/listPurchase.do")
	public String listPurchase( @ModelAttribute("search") Search search, HttpSession session,  Model model) throws Exception{
		
		System.out.println("/listPurchase.do");
		User user = (User)session.getAttribute("user");
		System.out.println("userId����Ǵ°�"+user.getUserId());
		search.setSearchKeyword(user.getUserId());
		if(search.getCurrentPage() ==0 ){
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		
		// Business logic ����
		Map<String , Object> map=purchaseService.getPurchaseList(search, search.getSearchKeyword());
		
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		System.out.println(resultPage);
		
		// Model �� View ����
		model.addAttribute("list", map.get("list"));
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("search", search);
		return "forward:/purchase/listPurchase.jsp";
	}
	
	@RequestMapping("/listSalePurchase.do")
	public String listSalePurchase( @ModelAttribute("search") Search search, HttpSession session,  Model model) throws Exception{
		
		System.out.println("/listSalePurchase.do");
		if(search.getCurrentPage() ==0 ){
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		
		// Business logic ����
		Map<String , Object> map=purchaseService.getSaleList(search);
		
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("saleCount")).intValue(), pageUnit, pageSize);
		System.out.println(resultPage);
		
		// Model �� View ����
		model.addAttribute("list", map.get("list"));
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("search", search);
		return "forward:/purchase/saleList.jsp";
	}
	
}