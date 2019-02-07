import java.io.File;

/**
 * Created by Frankline Sable on 28/12/2016. At Maseno university
 */
public class checkMP3Files {
    public checkMP3Files(File dir[]) {

        if (dir != null) {
            for (int i = 0; i < dir.length; i++) {
                if (!dir[i].isDirectory()) {
                    String extension = Utils.getExtension(dir[i]);
                    if (extension != null) {
                        if ((extension).toLowerCase().equals(Utils.mp3)) {
                           // countMp3++;
                        }
                    }
                }
            }
        } else {

        }
    }
}
