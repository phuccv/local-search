package com.snzck.localsearch.binpacking2d.servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import com.snzck.localsearch.FileFormatException;
import com.snzck.localsearch.InitMethod;
import com.snzck.localsearch.SearchMethod;
import com.snzck.localsearch.SearchModel;
import com.snzck.localsearch.binpacking2d.initstrage.BpInitMethodManager;
import com.snzck.localsearch.binpacking2d.initstrage.BpInitMethodType;
import com.snzck.localsearch.binpacking2d.io.BpData;
import com.snzck.localsearch.binpacking2d.io.BpDataManager;
import com.snzck.localsearch.binpacking2d.model.BpModelCombine;
import com.snzck.localsearch.search.SearchEventPool;
import com.snzck.localsearch.search.SearchMethodManager;
import com.snzck.localsearch.search.SearchMethodType;
import com.znzck.localsearch.system.ConfigManager;

/**
 * Servlet implementation class BinPacking2DServlet
 */
@WebServlet("/binpacking2d/control")
public class ControlServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String ACTION_NAME_INIT = "init";
	private static final String ACTION_NAME_START = "start";
	private static final String ACTION_NAME_STOP = "stop";
	
	
	private ArrayList<String> errorLists;
	
	private String action;
	private String initMethodName;
	private String searchMethodName;
	private int fileId;
	
    /**
     * Default constructor. 
     */
    public ControlServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		errorLists = new ArrayList<>();
		retrieveAction(request);
		
		if(! errorLists.isEmpty()){
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		if(action.equals(ACTION_NAME_INIT)){
			SearchModel model = null;
		
			// create new model
			BpDataManager dataMng = new BpDataManager(ConfigManager.INSTANT.getConfig());
			BpData data = dataMng.getDataById(fileId);
			try {
				model = new BpModelCombine(data);
			} catch (FileFormatException e) {
				e.printStackTrace();
			}
			
			BpInitMethodManager initMng
				= new BpInitMethodManager(ConfigManager.INSTANT.getConfig());
			InitMethod initMethod 
				= initMng.getInitMethod(model, BpInitMethodType.valueOf(initMethodName));
			model.initVariables(initMethod);

			SearchMethodManager searchMng
				= new SearchMethodManager(ConfigManager.INSTANT.getConfig());
			SearchMethod searchMethod = searchMng.getSearchMethod(model.getConstraintSystem(),
					model, SearchMethodType.valueOf(searchMethodName));
			
			SearchEventPool pool = new SearchEventPool(ConfigManager.INSTANT.getConfig());
			model.setEventPool(pool);
			
			model.initVariables(initMethod);
			
			// Set session
			HttpSession session = request.getSession();
			session.setAttribute("model", model);
			session.setAttribute("init", initMethod);
			session.setAttribute("data", data);
			session.setAttribute("search", searchMethod);
			session.setAttribute("pool", pool);
		
			
			response.setContentType("application/json");
			JSONObject jConfig = new JSONObject();
			
			// File information
			JSONObject jFile = new JSONObject();
			jFile.put("name", data.getFileName());
			jFile.put("id", fileId);
			jConfig.put("file", jFile);
			
			// Bin information
			JSONObject jBin = new JSONObject();
			try {
				jBin.put("height", data.getBinHeight());
				jBin.put("width", data.getBinWidth());
			} catch (JSONException | FileFormatException e) {
				e.printStackTrace();
			}
			jConfig.put("bin", jBin);
			
			// State information
			JSONObject jState = new JSONObject();
			jState.put("isRunning", false);
			jState.put("isReady", true);
			jConfig.put("state", jState);
			
			// Write to client
			response.getWriter().write(jConfig.toString());
			
		}else if(action.equals(ACTION_NAME_START)){
			SearchMethod search = (SearchMethod) request.getSession().getAttribute("search");
			if(search == null){
				System.out.println("No binpacking instant");
				//TODO
				return;
			}
			
			
			
			Thread main = new Thread(search);
//			bp.getEvents().clear(); // clear event list before start new
			main.start();
			
			response.setContentType("application/json");
			JSONObject jConfig = new JSONObject();
			
			// State information
			JSONObject jState = new JSONObject();
			jState.put("isRunning", true);
			jState.put("isReady", true);
			jConfig.put("state", jState);
			
			// Write to client
			response.getWriter().write(jConfig.toString());
			
		} else if(action.equals(ACTION_NAME_STOP)){
			System.out.println("STOP");
			SearchMethod search = (SearchMethod) request.getSession().getAttribute("search");
			if(search == null){
				//TODO
				return;
			}
			search.stop();
			
			response.setContentType("application/json");
			JSONObject jConfig = new JSONObject();
			
			// State information
			JSONObject jState = new JSONObject();
			jState.put("isRunning", true);
			jState.put("isReady", false);
			jConfig.put("state", jState);
			
			// Write to client
			response.getWriter().write(jConfig.toString());
			
		} else {
			System.err.println("INVALID ACTION");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response); // doGet instead
	}

	private void retrieveAction(HttpServletRequest request){
		action = request.getParameter("action");
		if(action == null){
			errorLists.add("Require action");
			return;
		}
		
		if(action.equals(ACTION_NAME_INIT)){
			retrieveFileId(request);
			retrieveInitMethod(request);
			retrieveSearchMethod(request);
		}
	}

	private void retrieveInitMethod(HttpServletRequest request){
		initMethodName = request.getParameter("initMode");
		
		if(initMethodName == null){
			errorLists.add("require init mode");
			return;
		}
	}
	
	private void retrieveSearchMethod(HttpServletRequest request){
		searchMethodName = request.getParameter("search");
		
		if(searchMethodName == null){
			errorLists.add("require search mode");
			return;
		}
	}
	
	private void retrieveFileId(HttpServletRequest request){
		String idString = request.getParameter("fileId");
		if(idString == null){
			errorLists.add("Require File Id");
			return;
		}
		
		try {
			fileId = Integer.parseInt(idString);
		} catch (NumberFormatException e) {
			errorLists.add("Invalid file id");
		}
	}
}
