package com.snzck.localsearch.binpacking2d.servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.snzck.localsearch.binpacking2d.initstrage.BpInitMethodType;
import com.snzck.localsearch.binpacking2d.io.BpDataManager;
import com.snzck.localsearch.search.SearchMethodType;
import com.znzck.localsearch.system.ConfigManager;

/**
 * Servlet implementation class ResourceServlet
 */
@WebServlet("/binpacking2d/fetch")
public class ResourceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private static final String[] RESOURCES = {
		"files",
		"init",
		"search"
	};
	
	private static final int RES_FILES = 0;
	private static final int RES_INIT = 1;
	private static final int RES_SEARCH_METHOD = 2;
	
	private ArrayList<String> errorList;
	private int res;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ResourceServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		errorList = new ArrayList<>();
		response.setContentType("application/json");
		
		retrieveAction(request);
		if(! errorList.isEmpty()){
			response.getWriter().write(new JSONArray().toString());
			return;
		}
	
		switch (res) {
		/*
		 * On files resource request, we return list file of Bp2D problem
		 */
		case RES_FILES:
			JSONArray fileList = new JSONArray();
			BpDataManager dataManager = new BpDataManager(ConfigManager.INSTANT.getConfig());
			for(int i = 0; i < dataManager.countData(); i++){
				JSONObject file = new JSONObject();
				file.put("id", i);
				file.put("name", dataManager.getDataById(i).getFileName());
				fileList.put(file);
			}
			response.getWriter().write(fileList.toString());
			break;
			
		/*
		 * On initial resource request, this return all initialize method
		 */
		case RES_INIT:
			JSONArray initMethodList = new JSONArray();
			for(BpInitMethodType type : BpInitMethodType.values()){
				initMethodList.put(type.name());
			}
			response.getWriter().write(initMethodList.toString());
			break;
		
		/*
		 * All search method 
		 */
		case RES_SEARCH_METHOD:
			JSONArray searchList = new JSONArray();
			for(SearchMethodType type : SearchMethodType.values()){
				searchList.put(type.name());
			}
			response.getWriter().write(searchList.toString());
			break;
			
		default:
			System.err.println("Unhandle resource");
			break;
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// doGet instead
		doGet(request, response);
	}

	
	private void retrieveAction(HttpServletRequest request){
		String actionName = request.getParameter("res");
		if(actionName == null){
			errorList.add("Require resource!");
			return;
		}
		
		for(int i = 0; i< RESOURCES.length; i++){
			if(actionName.equals(RESOURCES[i])){
				res = i;
				break;
			}
		}
	}
}
