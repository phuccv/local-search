package com.snzck.localsearch.binpacking2d.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.PooledConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.events.EventException;

import com.snzck.localsearch.FileFormatException;
import com.snzck.localsearch.SearchModel;
import com.snzck.localsearch.binpacking2d.io.BpData;
import com.snzck.localsearch.search.SearchEvent;
import com.snzck.localsearch.search.SearchEventPool;
import com.snzck.localsearch.search.SearchEventType;

/**
 * Servlet implementation class EventsServlet
 */
@WebServlet("/binpacking2d/events")
public class EventsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EventsServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("application/json");
		
		SearchEventPool pool = (SearchEventPool) request.getSession().getAttribute("pool");
		BpData data = (BpData) request.getSession().getAttribute("data");
		
		JSONObject jData = new JSONObject();
		JSONObject jState = new JSONObject();
		JSONArray jEvents = new JSONArray();
		
		if(pool == null){
			jState.put("ready", false);
			jState.put("running", false);
			jData.put("state", jState);
			
			jData.put("events", jEvents);
			
			response.getWriter().println(jData);
			return;
		}
		
		jState.put("ready", true);
		jState.put("running", true);
		jData.put("state", jState);
		
		JSONArray jEventList = new JSONArray();
		SearchEvent[] events = pool.listEvents();
		for(SearchEvent se : events){
			JSONObject jEvent = new JSONObject();
			jEvent.put("violations", se.getViolations());
			jEvent.put("objective", se.getObjective());
			jEvent.put("name", se.getType().name());
			
			JSONArray jItemList = new JSONArray();
			int[] widthList = data.getItemWidths();
			int[] heightList = data.getItemHeights();

			List<Integer[]> state = se.getState();
			
			Integer[] xPosList = state.get(0);
			Integer[] yPosList = state.get(1);
			Integer[] rotatedList = state.get(2);
			int length = xPosList.length;
			for(int i = 0; i< length; i++){
				JSONObject jItem = new JSONObject();
				jItem.put("xPos", xPosList[i]);
				jItem.put("yPos", yPosList[i]);
				jItem.put("width", widthList[i]);
				jItem.put("height", heightList[i]);
				jItem.put("rotated", rotatedList[i]);
				jItemList.put(jItem);
			}
			jEvent.put("items", jItemList);
			
			if(se.getType() == SearchEventType.INIT){
				try {
					JSONObject jBin = new JSONObject();
					jBin.put("width", data.getBinWidth());
					jBin.put("height", data.getBinHeight());
					jEvent.put("bin", jBin);
				} catch (JSONException | FileFormatException e) {
					// Do nothing up to now
				}
			}
			
			jEventList.put(jEvent);
		}
		
		jData.put("events", jEventList);
		
		response.getWriter().write(jData.toString());
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Do get instead
		doGet(request, response);
	}

}
