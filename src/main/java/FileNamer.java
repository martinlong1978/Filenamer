import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;

public class FileNamer
{

    static HashSet<String> extensions = new HashSet<String>();
    static DateFormat      df         = new SimpleDateFormat("yyyyMMdd-HHmmss");

    static
    {
        extensions.add("3gp");
        extensions.add("avi");
        extensions.add("jpg");
        extensions.add("mpg");
        extensions.add("mp4");
        extensions.add("mov");
        extensions.add("mts");
        extensions.add("gif");
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        walk(new File("/nas/gallery/martinpics/dump"));
    }

    public static void walk(File dir)
    {
        File[] files = dir.listFiles();
        for (File file : files)
        {
            if (file.isDirectory())
            {
                walk(file);
            }
            else
            {
                String name = file.getName();
                name = name.substring(name.lastIndexOf(".") + 1);
                if (extensions.contains(name.toLowerCase()))
                {
                    Date mod = new Date(file.lastModified());
                    if (name.equalsIgnoreCase("jpg"))
                    {
                        try
                        {
                            Metadata metadata = JpegMetadataReader.readMetadata(file);
                            Directory exif = metadata.getDirectory(ExifDirectory.class);
                            if (exif.containsTag(ExifDirectory.TAG_DATETIME_DIGITIZED))
                            {
                                Date moda = exif.getDate(ExifDirectory.TAG_DATETIME_DIGITIZED);
                                if (moda != null)
                                    mod = moda;
                            }
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                            System.exit(0);
                        }
                    }
                    File to = new File(dir.getAbsolutePath()
                            + "/"
                            + df.format(mod)
                            + "."
                            + name);
                    int i = 0;
                    while (to.exists() && !to.getName().equals(file.getName()))
                    {
                        to = new File(dir.getAbsolutePath()
                                + "/"
                                + df.format(mod)
                                + "-"
                                + (++i)
                                + "."
                                + name);
                    }
                    if (!to.exists())
                    {
                        System.out.println("Renaming: "
                                + file.getName()
                                + " to: "
                                + to.getName());
                        file.renameTo(to);
                    }
                }
            }
        }
    }

}
