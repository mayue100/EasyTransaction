package com.mayue.trans.protocol;

import java.io.Serializable;

/**
 * 幂等类型声明
 */
public interface IdempotentTypeDeclare {
    /**
     * 框架层幂等代码
     */
    public static final int IDENPOTENT_TYPE_FRAMEWORK = 0;

    /**
     * 业务层幂等代码
     */
    public static final int IDENPOTENT_TYPE_BUSINESS = 1;

    int getIdempotentType();

    /**
     * 全局事务id，用于幂等操作
     */
    TransactionId getParentTrxId();

    void setParentTrxId(TransactionId pTrxId);

    public static class TransactionId implements Serializable {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private String appId;
        private String busCode;
        private String trxId;

        public TransactionId(String appId, String busCode, String trxId) {
            super();
            this.appId = appId;
            this.busCode = busCode;
            this.trxId = trxId;
        }

        public String getAppId() {
            return appId;
        }

        public String getBusCode() {
            return busCode;
        }

        public String getTrxId() {
            return trxId;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((appId == null) ? 0 : appId.hashCode());
            result = prime * result
                    + ((busCode == null) ? 0 : busCode.hashCode());
            result = prime * result + ((trxId == null) ? 0 : trxId.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            TransactionId other = (TransactionId) obj;
            if (appId == null) {
                if (other.appId != null)
                    return false;
            } else if (!appId.equals(other.appId))
                return false;
            if (busCode == null) {
                if (other.busCode != null)
                    return false;
            } else if (!busCode.equals(other.busCode))
                return false;
            if (trxId == null) {
                if (other.trxId != null)
                    return false;
            } else if (!trxId.equals(other.trxId))
                return false;
            return true;
        }

    }


}
