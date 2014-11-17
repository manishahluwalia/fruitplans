package fruit.health.server.bizlogic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fruit.health.server.util.CommunicationChannel;
import fruit.health.shared.dto.LoginInfo;

public class RequestContextHolder
{
    private static ThreadLocal<HttpServletRequest> request = new ThreadLocal<>();
    private static ThreadLocal<HttpServletResponse> response = new ThreadLocal<>();
    private static ThreadLocal<CommunicationChannel> communicationChannel = new ThreadLocal<>();
    private static ThreadLocal<LoginInfo> creds = new ThreadLocal<>();
    
    public static void setRequestResponse(HttpServletRequest request, HttpServletResponse response)
    {
        RequestContextHolder.request.set(request);
        RequestContextHolder.response.set(response);
    }
    
    public static void setCommunicationChannel(CommunicationChannel communicationChannel)
    {
        RequestContextHolder.communicationChannel.set(communicationChannel);
    }

    public static void setCreds(LoginInfo creds)
    {
        RequestContextHolder.creds.set(creds);
    }
    
    public static void clear()
    {
        request.remove();
        response.remove();
        communicationChannel.remove();
        creds.remove();
    }
    
    public static HttpServletRequest getThreadLocalHttpRequest()
    {
        return request.get();
    }
    
    public static HttpServletResponse getThreadLocalHttpResponse()
    {
        return response.get();
    }
    
    public static CommunicationChannel getThreadLocalCommunicationChannel()
    {
        return communicationChannel.get();
    }

    public static LoginInfo getCreds()
    {
        return creds.get();
    }
}
