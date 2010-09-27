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

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.junit.Test;

public class FMPPMojoTests extends AbstractMojoTestCase {
	
	private final File outputDirectory = new File( "target/test/generated-sources/fmpp/" );
	private final File templateDirectory = new File( "src/test/resources/fmpp/templates/" );
	private final File cfgFile = new File( "src/test/resources/fmpp/" ); 
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Test
	public void testMojoExecution() throws Exception {

		Mojo mojo = getConfiguredMojo();
		mojo.execute();
		
		assertTrue( "Ouptput directory not created.", outputDirectory.exists() );
	}
	
	@Test
	public void testConfiguration() throws Exception {

		Mojo mojo = getConfiguredMojo();
		
        try
        {
        	setVariableValueToObject(mojo, "cfgFile", null);
        	mojo.execute();
        }
        catch (MojoExecutionException e) 
        {
        	assert( e.getMessage().contains( "cfgFile" ) );
        }
	}
	
	@Test
	public void testCodeGeneration() throws Exception {
		
		Mojo mojo = getConfiguredMojo();
		mojo.execute();
		
		assertTrue( outputDirectory.exists() );
		List<String> files = Arrays.asList( outputDirectory.list() );
		assertTrue( files.size() > 0);
		assertTrue( "Output file [AllNumeric.java] not created.", files.contains( "flowers.htm" ) );
	}
	
	/**
	 * @return The configured Mojo for testing.
	 * @throws Exception
	 */
	private Mojo getConfiguredMojo() throws Exception {

		File pluginXml = new File( getBasedir(), "src/test/resources/plugin-config.xml" );
		Mojo mojo = lookupMojo( "generate", pluginXml );
		
		setVariableValueToObject(mojo, "project", new MavenProjectStub());
		setVariableValueToObject(mojo, "outputDirectory", outputDirectory );
		setVariableValueToObject(mojo, "templateDirectory", templateDirectory);
		setVariableValueToObject(mojo, "cfgFile", cfgFile);
		
		return mojo;
	}
	
	/**
	 * cleans the output directory with all the artifacts created as a result of mojo execution
	 */
	private void cleanDirectory(final File directory) {
        if (directory != null) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        cleanDirectory( file );
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		cleanDirectory(outputDirectory);
	}
}
