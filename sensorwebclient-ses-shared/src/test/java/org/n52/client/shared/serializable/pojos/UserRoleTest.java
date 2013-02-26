package org.n52.client.shared.serializable.pojos;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.n52.shared.serializable.pojos.UserRole;


public class UserRoleTest {
    
    @Test public void
    shouldEvaluateCapitalAdminStringRoleAsAdmin()
    {
        assertThat(UserRole.isAdmin("ADMIN"), is(true));
    }
    
    @Test public void
    shouldNotEvaluateLowerCapitalAdminRoleAsAdmin()
    {
        assertThat(UserRole.isAdmin("admin"), is(false));
    }
    
    @Test public void
    shouldEvaluateCapitalUserAsAdmin()
    {
        assertThat(UserRole.isAdmin("USER"), is(false));
    }

}
