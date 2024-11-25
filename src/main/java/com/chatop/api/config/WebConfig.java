package com.chatop.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    /**
     * Configures resource handlers for serving static files.
     * <p>
     * This method allows the application to map specific URL paths to local file system directories,
     * enabling users to access files like images directly via HTTP requests.
     *
     * @param registry a registry object used to register resource handlers
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map the URL path "/upload/pictures/**" to a local directory on the file system.
        registry.addResourceHandler("/upload/pictures/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/upload/pictures/");
        // Explanation:
        // - "/upload/pictures/**" means that any request to this path will trigger this handler.
        // - "file:" specifies that the resources are stored on the local file system.
        // - System.getProperty("user.dir") gets the root directory of the project dynamically,
        //   making the configuration adaptable to different environments.

        // Example:
        // A file stored at /upload/pictures/image1.jpg will be accessible via:
        // http://localhost:3001/upload/pictures/webimage1.jpg
    }
}