package fruit.health.client;

import java.util.LinkedList;
import java.util.List;

import com.google.inject.Singleton;

import fruit.health.client.entities.PlanData;

@Singleton
public class GlobalsHolder {
	private LoginStateManager loginStateManager;
	private List<PlanData> plans = new LinkedList<PlanData>();
	public int planNumber=1;
	
	public LoginStateManager getLoginStateManager() {
		return loginStateManager;
	}
	public void setLoginStateManager(LoginStateManager loginStateManager) {
		this.loginStateManager = loginStateManager;
	}
	
	public void addPlan(PlanData plan) {
	    plans.add(plan);
	}
	
	public List<PlanData> getPlans() {
	    return plans;
	}
}
