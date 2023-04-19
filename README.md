# Image Optimization
## Introduction

This Spring Boot Maven project is designed to optimize images in a specified directory. It can be used to compress and convert images to the WebP format, which reduces file sizes while maintaining image quality. The project uses the 'webp-imageio' library provided by the following dependency:
```
<dependency>
    <groupId>org.sejda.imageio</groupId>
    <artifactId>webp-imageio</artifactId>
    <version>${webp.version}</version>
    <exclusions>
        <exclusion>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

## Table of Contents
- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
- [Classes](#classes)
## Requirements
- Java 8
- Maven
## Installation
1. Clone the repository:
```bash
   git clone https://github.com/your-username/image-optimizer.git
```
2. Change into the project directory:

```bash
cd image-optimizer
```
3. Build the project using Maven:
```bash
mvn clean install
```
## Usage
To use the image optimizer, send a POST request to the application with the directory path as a JSON payload. The request should include the `dirPath` parameter:
```json
{
  "dirPath": "/path/to/your/image/directory"
}
```
The application will then optimize all images in the specified directory.
### Classes

1. ImageOptimizationController: Handles incoming HTTP requests and passes the directory path to the ImageOptimizationService.
2. OptimizeImagesDirPathRequest: Defines the request object containing the directory path.
3. ImageOptimizationService: Orchestrates the optimization process by calling the ImageOptimizer.
4. ImageOptimizer: Optimizes all images in the specified directory and saves them in the WebP format.
5. DirectoryHelper: Retrieves files from the specified directory.
6. ImageReaderHelper: Reads images from the input file path.
7. ImageWriterHelper: Writes optimized images to the output file path with adjustable compression quality (e.g., 0.4F).
