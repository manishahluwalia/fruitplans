package fruit.health.shared.dto;


/* For the password: Algo, salt, iteration, hash of passwd */
public enum PasswordHashAlgo {
    /**
     * <code>Hash = SHA256( UTF8(Email) . UTF8(Password) )</code> 
     */
    SHA256_ONEITER_EMAILSALT("SHA-256"), //$NON-NLS-1$
    ;
    
    private String hashAlgoName;

    private PasswordHashAlgo(String hashAlgoName) {
        this.hashAlgoName = hashAlgoName;
    }
    
    public String getHashAlgorithm() {
        return hashAlgoName;
    }
}
