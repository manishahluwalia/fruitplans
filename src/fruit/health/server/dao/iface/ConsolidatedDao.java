
package fruit.health.server.dao.iface;

import java.util.List;

import com.google.inject.ImplementedBy;

import fruit.health.server.dao.impl.ConsolidatedDaoImpl;
import fruit.health.shared.entities.Table;
import fruit.health.shared.entities.User;

/**
 * Instead of having one DAO class for each entity, we consolidate them into
 * one. Avoid useless code.
 */
@ImplementedBy(ConsolidatedDaoImpl.class)
public interface ConsolidatedDao
{
    /**
     * Given the system's user id, get the {@link User} (the DB entry for the
     * user).
     *
     * @param userId
     * @return User
     */
    User getUserById (long userId);

    /**
     * Create the new user who's (filled in, new) {@link User} entry is given.
     *
     * @param user
     */
    void createNewUser (User user);

    /**
     * Given the email id, find the {@link User} (the DB entry for the user).
     *
     * @param email
     * @return
     */
    User getUserByEmail (String email);

    /**
     * Given the verifier, find the associated user
     */
    User getUserByVerifier (String verifier);

    /**
     * Update the user entry given.
     *
     * @param user
     */
    void update (User user);

    /**
     * Delete the given user
     *
     * @param user
     */
    void delete (User user);
    
    
    void createNewTable(Table table);

    List<Table> getTables(int firstTableIndex, int tablesInOnePage);

    Table getTableById(long tableId);

    void update(Table table);

    List<Table> getTablesLoadingToDb();
}
