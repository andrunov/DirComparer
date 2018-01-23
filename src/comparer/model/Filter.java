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

    /*default constructor*/
    public Filter() {
    }

    /*constructor*/
    public Filter(String[] extensions) {
        this.extensions = extensions;
    }

    /*getters and setters*/

    public String[] getExtensions() {
        return extensions;
    }

    public void setExtensions(String[] extensions) {
        this.extensions = extensions;
    }

    /*accept only files with fitting extensions*/
    @Override
    public boolean accept(File dir, String name){
        if (new File(dir.getAbsolutePath()+ "\\" + name).isDirectory()) return true;

        String path = name.toLowerCase();
        for (String extension : extensions) {
            if ((path.endsWith(extension) && (path.charAt(path.length()
                    - extension.length() - 1)) == '.')) {
                return true;
            }
        }

        return false;
    }

}
