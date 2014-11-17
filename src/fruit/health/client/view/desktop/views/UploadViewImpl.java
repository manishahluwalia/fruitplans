package fruit.health.client.view.desktop.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xhr.client.XMLHttpRequest;

import fruit.health.client.view.UploadView;
import fruit.health.client.view.UploadView.Presenter;
import fruit.health.client.view.desktop.BaseViewImpl;
import fruit.health.shared.dto.UploadParams;
import fruit.health.shared.util.SharedUtils;

public class UploadViewImpl extends BaseViewImpl<Presenter> implements UploadView
{
    private static class FormData extends JavaScriptObject
    {
        protected FormData() {}
        public final native void append(String key, String value)/*-{
            this.append(key, value);
        }-*/; 
    }

    private static UploadViewUiBinder uiBinder = GWT.create(UploadViewUiBinder.class);
    
    @UiField InputElement description;

    private JavaScriptObject oldFile;
    private JavaScriptObject dropZone;

    @UiTemplate("UploadView.ui.xml")
    interface UploadViewUiBinder extends UiBinder<Widget, UploadViewImpl> {
    }

    public UploadViewImpl()
    {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setupUploadParams(UploadParams params)
    {
        GWT.log("setup called");
    }
    
    @UiHandler("submit")
    public void onSubmit(ClickEvent e)
    {
        GWT.log("submit hit  ");
        
        String description = this.description.getValue();
        if (SharedUtils.isEmpty(description))
        {
            viewMaster.alertDialog("Empty description", "Please enter the data description", null, null); // XXX internationalize
            return;
        }
        
//        String schema = this.schema.getValue();
//        if (SharedUtils.isEmpty(schema))
//        {
//            viewMaster.alertDialog("Empty schema", "Please enter the schema description (comma separted list of field:type)", null, null); // XXX internationalize
//            return;
//        }

        presenter.submitClicked(description);
        
    }
    
    @Override
    public void submitForm()
    {
        presenter.submitDone();
    }

    private void onDropZoneAddedFile(JavaScriptObject file)
    {
        if (null!=oldFile)
        {
            removeFile(oldFile);
        }
        oldFile=file;
        
    }
    private native void removeFile(JavaScriptObject oldFile)
    /*-{
        this.@fruit.health.client.view.desktop.views.UploadViewImpl::dropZone.removeFile(oldFile);
    }-*/;

    private void onDropZoneError(JavaScriptObject file, String errorMsg, XMLHttpRequest xhr)
    {
        
    }

    private void onDropZoneSending(JavaScriptObject file, XMLHttpRequest xhr, FormData formData)
    {
        xhr.setRequestHeader("X-Authorization", "XXX");
        formData.append("key", "value");
    }

    private void onDropZoneSuccess(JavaScriptObject file, String response)
    {
        
    }

    @Override
    public native void initializeDropZone(String uploadTo)
    /*-{
        var self = this;
        var dropZone = new $wnd.Dropzone("div#dropzone", {url: uploadTo, uploadMultiple: false, autoProcessQueue: false});
        this.@fruit.health.client.view.desktop.views.UploadViewImpl::dropZone = dropZone;
        dropZone.on("addedfile", function (file) {
            self.@fruit.health.client.view.desktop.views.UploadViewImpl::onDropZoneAddedFile(Lcom/google/gwt/core/client/JavaScriptObject;)(file);
        });
        dropZone.on("error", function (file, errorMsg, xhr) {
            self.@fruit.health.client.view.desktop.views.UploadViewImpl::onDropZoneError(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Lcom/google/gwt/xhr/client/XMLHttpRequest;)(file, errorMsg, xhr);
        });
        dropZone.on("sending", function (file, xhr, formData) {
            self.@fruit.health.client.view.desktop.views.UploadViewImpl::onDropZoneSending(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/xhr/client/XMLHttpRequest;Lfruit/health/client/view/desktop/views/UploadViewImpl$FormData;)(file, xhr, formData);
        });
        dropZone.on("success", function (file, resp) {
            self.@fruit.health.client.view.desktop.views.UploadViewImpl::onDropZoneSuccess(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(file, resp);
        });
    }-*/;
}
