package fruit.health.client.view;

import fruit.health.client.mvp.BaseView;
import fruit.health.shared.dto.UploadParams;

public interface UploadView extends BaseView<UploadView.Presenter>
{

    public interface Presenter
    {
        void uploadClicked();
        void submitClicked(String description);
        void submitDone();
    }

    void setupUploadParams(UploadParams params);
    void initializeDropZone(String uploadTo);
    void submitForm();

}
