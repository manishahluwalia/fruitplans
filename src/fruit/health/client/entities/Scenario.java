package fruit.health.client.entities;

public class Scenario
{
    private static final int AVG_COST_PER_DOC_VISIT = 100;
    private static final int AVG_COST_PER_DAY_IN_HOSPI = 10000;
    private static final int AVG_COST_PER_RX = 50;
    public Integer age;
    public Boolean isFemale;
    public Boolean gettingPregnant;
    public Integer numDocVisits;
    public Integer numDaysInHospital;
    public Integer numRxs;
    
    
    public int getMedicalExpenses()
    {
        // TODO: take all the info into account and make more intelligent calculations
        return numDocVisits * AVG_COST_PER_DOC_VISIT +
                numDaysInHospital * AVG_COST_PER_DAY_IN_HOSPI +
                numRxs * AVG_COST_PER_RX;
    }
}
