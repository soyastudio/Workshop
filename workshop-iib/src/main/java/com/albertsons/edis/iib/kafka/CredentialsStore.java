package com.albertsons.edis.iib.kafka;


import com.ibm.broker.connector.ContainerServices;
import com.ibm.broker.connector.PasswordCredential;
import com.ibm.broker.connector.SecurityIdentity;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbRecoverableException;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CredentialsStore {
    private static final String className = "CredentialsStore";
    private static CredentialsStore instance = new CredentialsStore();
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private String credentialName;
    private LinkedList<CredentialsStore.KafkaCredentials> credentialList = new LinkedList();

    private CredentialsStore() {
    }

    public static CredentialsStore getInstance() {
        return instance;
    }

    public CredentialsStore.KafkaCredentials saveCredentials(SecurityIdentity var1) throws MbException {
        CredentialsStore.KafkaCredentials var2 = null;
        ContainerServices.writeServiceTraceEntry("CredentialsStore", "saveCredentials", var1.getName());

        try {
            if (var1.getName() != null && var1.getName().length() > 0 && var1.getCredential() != null && !(var1.getCredential() instanceof PasswordCredential)) {
                throw new MbRecoverableException("CredentialsStore", "saveCredentials", "BIP", "3884", "Invalid credential type", new String[]{var1.getCredential().getClass().getName()});
            }

            this.lock.writeLock().lock();

            try {
                if (!this.credentialList.isEmpty() && !var1.getName().equals(((CredentialsStore.KafkaCredentials)this.credentialList.getFirst()).getSecurityIdentity().getName())) {
                    throw new MbRecoverableException("CredentialsStore", "saveCredentials", "BIP", "3885", "Different credential is already saved", new String[]{var1.getName(), ((CredentialsStore.KafkaCredentials)this.credentialList.getFirst()).getSecurityIdentity().getName()});
                }

                var2 = new CredentialsStore.KafkaCredentials(var1);
                this.credentialList.add(var2);
            } finally {
                this.lock.writeLock().unlock();
            }
        } finally {
            ContainerServices.writeServiceTraceExit("CredentialsStore", "saveCredentials", "");
        }

        return var2;
    }

    public void releaseCredentials(CredentialsStore.KafkaCredentials var1) throws MbRecoverableException {
        ContainerServices.writeServiceTraceEntry("CredentialsStore", "releaseCredentials", this.credentialName);

        try {
            this.lock.writeLock().lock();
            if (!this.credentialList.remove(var1)) {
                throw new MbRecoverableException("CredentialsStore", "releaseCredentials", "BIP", "3886", "No credentials saved", new String[0]);
            }
        } finally {
            this.lock.writeLock().unlock();
            ContainerServices.writeServiceTraceExit("CredentialsStore", "releaseCredentials", "");
        }

    }

    public String getCredentialName() {
        String var1 = "";
        ContainerServices.writeServiceTraceEntry("CredentialsStore", "getCredentialName", "");
        this.lock.readLock().lock();

        try {
            if (!this.credentialList.isEmpty()) {
                var1 = ((CredentialsStore.KafkaCredentials)this.credentialList.getFirst()).getName();
            }
        } catch (Exception var6) {
        } finally {
            this.lock.readLock().unlock();
            ContainerServices.writeServiceTraceExit("CredentialsStore", "getUserName", var1);
        }

        return var1;
    }

    public String getUserName() throws MbException {
        String var1 = "";
        ContainerServices.writeServiceTraceEntry("CredentialsStore", "getUserName", "");
        this.lock.readLock().lock();

        try {
            SecurityIdentity var2 = ((CredentialsStore.KafkaCredentials)this.credentialList.getFirst()).getSecurityIdentity();
            var1 = ((PasswordCredential)((PasswordCredential)var2.getCredential())).getUserName();
            if (var1 == null) {
                var1 = "";
            }
        } finally {
            this.lock.readLock().unlock();
            ContainerServices.writeServiceTraceExit("CredentialsStore", "getUserName", var1);
        }

        return var1;
    }

    public char[] getPassword() throws MbException {
        Object var1 = null;
        ContainerServices.writeServiceTraceEntry("CredentialsStore", "getPassword", "");
        this.lock.readLock().lock();

        char[] var6;
        try {
            SecurityIdentity var2 = ((CredentialsStore.KafkaCredentials)this.credentialList.getFirst()).getSecurityIdentity();
            var6 = ((PasswordCredential)((PasswordCredential)var2.getCredential())).getPassword();
            if (var6 == null) {
                var6 = "".toCharArray();
            }
        } finally {
            this.lock.readLock().unlock();
            ContainerServices.writeServiceTraceExit("CredentialsStore", "getPassword", "");
        }

        return var6;
    }

    public int getCredentialCount() {
        return this.credentialList.size();
    }

    public class KafkaCredentials {
        private SecurityIdentity securityIdentity;

        public KafkaCredentials(SecurityIdentity var2) {
            this.securityIdentity = var2;
        }

        public SecurityIdentity getSecurityIdentity() {
            return this.securityIdentity;
        }

        public String getName() {
            return this.securityIdentity.getName();
        }
    }
}

