package fruit.health.shared.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UploadParams implements IsSerializable
{
    public String url;
    public String key;
    public String acl;
    public String bucket;
    public String accessId;
    public String redirectUrlBase;
    public String policy;
    public String signature;
}
