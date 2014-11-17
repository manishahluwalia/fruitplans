package fruit.health.shared.dto;

public enum TableState
{
    LOADING_TO_STORAGE,
    LOADING_TO_STORAGE_FAILED,
    DECIPHERING,
    DECIPHERING_FAILED,
    CLEANING,
    CLEANING_FAILED,
    LOADING_TO_DB,
    LOADING_TO_DB_FAILED,
    READY,
    DELETED
}
