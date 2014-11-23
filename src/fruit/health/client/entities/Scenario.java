package fruit.health.client.entities;

public class Scenario
{
    private static final int AVG_COST_PER_DOC_VISIT = 100;
    private static final int AVG_COST_PER_HOSPITALIZATION = 10000;
    private static final int AVG_COST_PER_RX = 100;
    public String name;
    public Integer age;
    public Boolean isFemale;
    public Boolean gettingPregnant;
    public Integer numDocVisits;
    public Integer numHospitalizations;
    public Integer numRxs;
    
    
    public int getMedicalExpenses()
    {
        // TODO: take all the info into account and make more intelligent calculations
        return numDocVisits * AVG_COST_PER_DOC_VISIT +
                numHospitalizations * AVG_COST_PER_HOSPITALIZATION +
                numRxs * AVG_COST_PER_RX;
    }
}
