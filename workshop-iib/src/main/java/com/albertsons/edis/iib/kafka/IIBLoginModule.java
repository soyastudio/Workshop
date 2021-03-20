package com.albertsons.edis.iib.kafka;

import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbService;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import org.apache.kafka.common.security.plain.internals.PlainSaslServerProvider;

public class IIBLoginModule implements LoginModule {
    private boolean successfullyAcquiredCredentials;

    public IIBLoginModule() {
    }

    public void initialize(Subject var1, CallbackHandler var2, Map<String, ?> var3, Map<String, ?> var4) {
        this.successfullyAcquiredCredentials = false;
        CredentialsStore var5 = CredentialsStore.getInstance();

        try {
            String var6 = var5.getUserName();
            if (var6 != null && var6.length() > 0) {
                var1.getPublicCredentials().add(var6);
                this.successfullyAcquiredCredentials = true;
            }

            String var7 = new String(var5.getPassword());
            if (var7 != null && var7.length() > 0) {
                var1.getPrivateCredentials().add(var7);
            }
        } catch (MbException var8) {
            this.successfullyAcquiredCredentials = false;
        }

    }

    public boolean login() throws LoginException {
        CredentialsStore var1 = CredentialsStore.getInstance();
        if (!this.successfullyAcquiredCredentials) {
            String var2 = "3883";
            String[] var3 = new String[]{var1.getCredentialName()};

            try {
                MbService.logError(this, "getUserName", "BIPmsgs", var2, "login", var3);
            } catch (MbException var5) {
            }

            throw new LoginException("Failed to acquire credentials");
        } else {
            return this.successfullyAcquiredCredentials;
        }
    }

    public boolean logout() throws LoginException {
        return true;
    }

    public boolean commit() throws LoginException {
        return true;
    }

    public boolean abort() throws LoginException {
        return false;
    }

    static {
        PlainSaslServerProvider.initialize();
    }
}

