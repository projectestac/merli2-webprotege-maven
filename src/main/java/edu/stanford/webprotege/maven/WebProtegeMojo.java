package edu.stanford.webprotege.maven;

import com.thoughtworks.qdox.JavaProjectBuilder;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 26 Apr 16
 */
@Mojo(name = "generatePortletFactory", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class WebProtegeMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @SuppressWarnings("unchecked")
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        MavenSourceWriter writer = new MavenSourceWriter(project, getLog());

        Set<PortletTypeDescriptor> tds = new HashSet<>();
        Set<PortletModuleDescriptor> mds = new HashSet<>();

        try {
            for(Object compileSourceRoot : new ArrayList<>(project.getCompileSourceRoots())) {
                String sourceRoot = (String) compileSourceRoot;
                JavaProjectBuilder builder = getProjectBuilder(sourceRoot);

                tds.addAll(getTypeDescriptors(builder));
                mds.addAll(getModuleDescriptors(builder));
            }

            WebProtegeCodeGeneratorVelocityImpl generator =
                new WebProtegeCodeGeneratorVelocityImpl(tds, mds, writer);

            generator.generate();

            logPortletDescriptors(tds);
        } catch (IOException e) {
            getLog().error(e.getMessage(), e);
        }
    }


    private Set<PortletTypeDescriptor> getTypeDescriptors(JavaProjectBuilder builder) {
        AnnotatedPortletClassExtractor extractor =
            new AnnotatedPortletClassExtractor(builder);

        Set<AnnotatedPortletClass> portletClasses =
            extractor.findAnnotatedPortletClasses();

        return portletClasses.stream().map(c ->
            new PortletTypeDescriptorBuilder(
                c.getJavaClass(), c.getJavaAnnotation()).build()
            ).collect(toSet());
    }


    private Set<PortletModuleDescriptor> getModuleDescriptors(JavaProjectBuilder builder) {
        AnnotatedPortletModuleClassExtractor moduleClassExtractor =
            new AnnotatedPortletModuleClassExtractor(builder);

        return moduleClassExtractor.findAnnotatedProjectModulePlugins();
    }


    private JavaProjectBuilder getProjectBuilder(String sourceRoot) {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.setErrorHandler(e -> getLog().info("[WebProtegeMojo] Couldn't parse file: " + e));
        builder.addSourceTree(new File(sourceRoot));
        return builder;
    }

    private void logPortletDescriptors(Set<PortletTypeDescriptor> descriptors) {
        for(PortletTypeDescriptor d : descriptors) {
            getLog().info("[Portlet] " + d.getId());
        }
    }
}
