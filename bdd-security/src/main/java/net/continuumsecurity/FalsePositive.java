package net.continuumsecurity;

public class FalsePositive {
    String url;
    String parameter;
    int cweId = 0;
    int wascId = 0;

    public FalsePositive(String url, String parameter, int cweId, int wascId) {
        this.url = url;
        this.parameter = parameter;
        this.cweId = cweId;
        this.wascId = wascId;
    }

    public FalsePositive(String url, String parameter, String cweId, String wascId) {
        this.url = url;
        this.parameter = parameter;
        try {
            this.cweId = Integer.parseInt(cweId);
        } catch (NumberFormatException e) {

        }
        try {
            this.wascId = Integer.parseInt(cweId);
        } catch (NumberFormatException e) {

        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public int getCweId() {
        return cweId;
    }

    public void setCweId(int cweId) {
        this.cweId = cweId;
    }

    public boolean matches(String url, String parameter, int cweid, int wascId) {
        if (this.url != null && this.url.equals(url) && this.parameter != null && this.parameter.equals(parameter) && (this.cweId == cweId || this.wascId == this.wascId)) return true;
        return false;
    }
}
