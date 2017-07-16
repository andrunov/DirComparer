package comparer.model;

import java.io.File;
import java.io.FilenameFilter;

/**
 * class for adjust file filter
 */
public class Filter implements FilenameFilter
{
    /*file extensions*/
    private String[] extensions;

    public Filter() {
    }

    public Filter(String[] extensions) {
        this.extensions = extensions;
    }

    public String[] getExtensions() {
        return extensions;
    }

    public void setExtensions(String[] extensions) {
        this.extensions = extensions;
    }

    @Override
    public boolean accept(File dir, String name){
         String path = name.toLowerCase();
         for (String extension : extensions)
         {
             if ((path.endsWith(extension) && (path.charAt(path.length()
                     - extension.length() - 1)) == '.'))
             {
                 return true;
             }
         }

        return false;
    }

}
