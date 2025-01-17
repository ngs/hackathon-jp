package android.social;

import org.opensocial.data.OpenSocialActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class NotificationGenerator {

	private Context mContext;

	public NotificationGenerator(Context context) {
		mContext = context;
	}
	
	public void add(OpenSocialActivity activity)
	{
        NotificationManager notificationManager;
        notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification;
        notification = new Notification(R.drawable.stat_sample,  "Notification From Social Site", System.currentTimeMillis());

        Intent intent = createIntent(activity);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
        notification.setLatestEventInfo(mContext, "Friend Sync Service", activity.getTitle(), contentIntent);
        notificationManager.notify(Integer.parseInt(activity.getId()), notification);
	}
	
	private Intent createIntent(OpenSocialActivity activity) {
		Intent intent = null;
		String body = activity.getBody();
		if (body != null) {
			int index = body.indexOf("http:");
			if (index != -1) {
				intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(body.substring(index)));
				return intent;
			}
			index = body.indexOf("https:");
			if (index != -1) {
				intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(body.substring(index)));
				return intent;
			}
			index = body.indexOf("geo:");
			if (index != -1) {
//				intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(body.substring(index)));
				intent = new Intent(mContext, MapViewerActivity.class);
				intent.putExtra("comment", body.substring(0, index));
				intent.putExtra("position", body.substring(index));
				return intent;
			}
		}
		return null;
	}
}
