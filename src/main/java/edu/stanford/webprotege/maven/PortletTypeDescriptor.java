package edu.stanford.webprotege.maven;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.Comparator;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 26 Apr 16
 */
public class PortletTypeDescriptor implements Comparable<PortletTypeDescriptor> {

    private final String packageName;

    private final String canonicalClassName;

    private final String simpleName;

    private final String id;

    private final String title;

    private final String tooltip;

    /** MD5 hash of the title */
    private final String titleMD5;

    /** MD5 hash of the tooltip */
    private final String tooltipMD5;

    /** MD5 hashing function */
    private final HashFunction md5 = Hashing.md5();


    /**
     * Constructs a PortletTypeDescriptor that describes a type of portlet
     * @param canonicalClassName The portlet class name.  Not {@code null}.
     * @param simpleName The simple class name of the portlet.  Not {@code null}.
     * @param packageName The package name of the portlet.  Not {@code null}.
     * @param id The porlet id. Not {@code null}.
     * @param title The portlet title.  Not {@code null}.
     * @param tooltip The tooltip for the portlet.  Not {@code null}.
     */
    public PortletTypeDescriptor(String canonicalClassName,
                                 String simpleName,
                                 String packageName,
                                 String id,
                                 String title,
                                 String tooltip) {
        this.canonicalClassName = checkNotNull(canonicalClassName);
        this.simpleName = checkNotNull(simpleName);
        this.id = checkNotNull(id);
        checkArgument(!id.startsWith("\"") && !id.endsWith("\""));
        this.title = checkNotNull(title);
        checkArgument(!title.startsWith("\"") && !title.endsWith("\""));
        this.packageName = checkNotNull(packageName);
        this.tooltip = checkNotNull(tooltip);
        checkArgument(!tooltip.startsWith("\"") && !tooltip.endsWith("\""));

        titleMD5 = makeHash(title, id + "title");
        tooltipMD5 = makeHash(tooltip, id + "tooltip");
    }


    /**
     * Gets the simple name of the portlet class.
     * @return A string representing the simple name.  Not {@code null}.
     */
    public String getSimpleName() {
        return simpleName;
    }

    /**
     * Gets the package name of the portlet class.
     * @return A string representing the portlet class package name.  Not {@code null}.
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Gets the canonical class name of the portlet.
     * @return The canonical class name of the portlet.  Not {@code null}.
     */
    public String getCanonicalClassName() {
        return canonicalClassName;
    }

    /**
     * Gets the id of the portlet.
     * @return The portlet id.  Not {@code null}.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the title of the portlet.
     * @return The portlet title.  Not {@code null}.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the title escaped as a Java source code string.
     * @return The escaped title.
     */
    public String getEscapedTitle() {
        return StringEscapeUtils.escapeJava(title);
    }


    /**
     * Returns the MD5 hash of the title.
     */
    public String getTitleMD5() {
        return titleMD5;
    }


    /**
     * Gets the tooltip of the portlet.
     * @return The portlet tooltip.  Not {@code null}.
     */
    public String getTooltip() {
        return tooltip;
    }

    /**
     * Gets the tooltip escaped as a Java source code string.
     * @return The escaped tooltip.
     */
    public String getEscapedTooltip() {
        return StringEscapeUtils.escapeJava(tooltip);
    }


    /**
     * Returns the MD5 hash of the tooltip text.
     */
    public String getTooltipMD5() {
        return tooltipMD5;
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(canonicalClassName, id, title);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PortletTypeDescriptor)) {
            return false;
        }
        PortletTypeDescriptor other = (PortletTypeDescriptor) obj;
        return this.canonicalClassName.equals(other.canonicalClassName)
                && this.simpleName.equals(other.simpleName)
                && this.packageName.equals(other.packageName)
                && this.id.equals(other.id)
                && this.title.equals(other.title)
                && this.tooltip.equals(other.tooltip);
    }


    @Override
    public String toString() {
        return toStringHelper("PortletTypeDescriptor")
                .addValue(id)
                .addValue(title)
                .addValue(canonicalClassName)
                .addValue(packageName)
                .addValue(simpleName)
                .toString();
    }

    @Override
    public int compareTo(PortletTypeDescriptor o) {
        return this.canonicalClassName.compareTo(o.canonicalClassName);
    }


    /**
     * Generates a MD5 hash for the given portlet text. This method
     * returns a hash string that can be used as a translation key.
     *
     * @param text      Text to hash
     * @param context   Translation context
     *
     * @return          Unique hash
     */
    private String makeHash(String text, String context) {
        String string = context + text;
        HashCode hash = md5.hashString(string, Charsets.UTF_8);

        return String.valueOf(hash).toUpperCase();
    }

}
