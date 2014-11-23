package fruit.health.client;

import java.util.LinkedList;
import java.util.List;

import com.google.inject.Singleton;

import fruit.health.client.entities.PlanData;
import fruit.health.client.entities.Scenario;

@Singleton
public class GlobalsHolder {
	private LoginStateManager loginStateManager;
	private List<PlanData> plans = new LinkedList<PlanData>();
	public int planNumber=1;
	private final Scenario customScenario;
	
	public GlobalsHolder() {
	    customScenario = new Scenario();
	    // DEFAULTS FOR THE CUSTOM SCENARIO
	    customScenario.isFemale = true;
	    customScenario.age = 40;
	    customScenario.gettingPregnant = false;
	    customScenario.numDocVisits = 2;
	    customScenario.numHospitalizations = 0;
	    customScenario.numRxs = 6;
	}
	
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
	
    public Scenario getCustomScenario()
    {
        return customScenario;
    }

    public void setPlans(List<PlanData> plans)
    {
        this.plans = plans;
    }
}
