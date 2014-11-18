package fruit.health.client.entities;

public class PlanData
{
    public String planName;
    public Integer premium;
    public Integer copay; // %age
    public Integer deductible;
    public Integer oopMax;
    
    public PlanData clone(PlanData clone) {
        clone.planName = planName;
        clone.copay = copay;
        clone.premium = premium;
        clone.deductible = deductible;
        clone.oopMax = oopMax;
        return clone;
    }
}
