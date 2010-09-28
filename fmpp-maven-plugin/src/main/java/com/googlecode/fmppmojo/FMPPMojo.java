/*******************************************************************************
 * The MIT License
 * 
 * Copyright (c) 2010, Faisal Feroz
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.googlecode.fmppmojo;

/**
 * This is a Maven Plugin front end for the FMPP. The plugin provides functionality for generating
 * code, test code, resources and test resources for a maven project. 
 * 
 * @author Faisal Feroz
 */
import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import fmpp.ProcessingException;
import fmpp.progresslisteners.ConsoleProgressListener;
import fmpp.setting.SettingException;
import fmpp.setting.Settings;
import fmpp.util.MiscUtil;

/**
 * 
 * Generates artifacts as configured. The output is placed in configured output directory.
 * 
 * @goal generate
 * @phase generate-sources
 */
public class FMPPMojo extends AbstractMojo {

	private static final String DEFAULT_ERROR_MSG = "\"%s\" is a required parameter. ";

	/** 
	 * Project instance, needed for attaching the build info file. 
	 * Used to add new source directory to the build. 
	 * @parameter default-value="${project}"  
	 * @required 
	 * @readonly 
	 * @since 1.0
	 **/
	private MavenProject project;

	/**
	 * Location of the output files.
	 * 
	 * @parameter  default-value="${project.build.directory}/generated-sources/fmpp/"
	 * @required
	 */
	private File outputDirectory;
	
	/**
	 * Location of the FreeMarker template files.
	 * 
	 * @parameter  default-value="src/main/resources/fmpp/templates/"
	 * @required
	 * @since 1.0
	 */
	private File templateDirectory;
	
	/**
	 * Location of the FreeMarker config file.
	 * 
	 * @parameter  default-value="src/main/resources/fmpp/config.fmpp"
	 * @required
	 * @since 1.0
	 */
	private File cfgFile;

	public void execute() throws MojoExecutionException, MojoFailureException {
		
		validateParameters();
		
		if ( !outputDirectory.exists() ) outputDirectory.mkdirs();
		
		try {
			Settings settings = new Settings( new File(".") );
			settings.set( "sourceRoot", templateDirectory.getAbsolutePath() );
			settings.set( "outputRoot", outputDirectory.getAbsolutePath() );
			
			settings.load( cfgFile );
			settings.addProgressListener( new ConsoleProgressListener() );
			settings.execute();

			getLog().info( "Done" );

		} catch (SettingException e) {

			getLog().error( MiscUtil.causeMessages(e) );
			throw new MojoFailureException( MiscUtil.causeMessages(e), e);

		} catch (ProcessingException e) {

			getLog().error( MiscUtil.causeMessages(e) );
			throw new MojoFailureException( MiscUtil.causeMessages(e), e);
		} 
		finally {
			// add the output directory path to the project source directories
			project.addCompileSourceRoot( outputDirectory.getAbsolutePath() );
			project.addTestCompileSourceRoot( outputDirectory.getAbsolutePath() );
		}
	}

	/**
	 * Validates the parameters that are required by the plug-in.
	 * 
	 * @throws MojoExecutionException Throws this exception when the data is not valid. 
	 */
	private void validateParameters() throws MojoExecutionException {

		if ( project == null ) throw new MojoExecutionException( String.format( DEFAULT_ERROR_MSG, "project") + "This plugin can be used only inside a project." );

		if ( outputDirectory == null) throw new MojoExecutionException( String.format( DEFAULT_ERROR_MSG, "outputDirectory") );
		
		if ( templateDirectory == null) throw new MojoExecutionException( String.format( DEFAULT_ERROR_MSG, "templateDirectory") );
		
		if ( cfgFile == null) throw new MojoExecutionException( String.format( DEFAULT_ERROR_MSG, "cfgFile") );
	}
}
