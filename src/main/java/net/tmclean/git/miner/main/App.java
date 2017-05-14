package net.tmclean.git.miner.main;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.tmclean.git.miner.GitDeltaSummary;
import net.tmclean.git.miner.GitDeltaSummaryGenerator;

public class App implements Callable<GitDeltaSummary>
{
	private static final String WORKING_DIR = "C:/Users/Tom McLean/Desktop/fdd-data-api/filecabinets/filecabinets-data-sql";

	private static final String START_REF = "develop";
//	private static final String MIDDLE_REF = "master";
	private static final String LOWEST_REF = "9.2.7.6";
	private static final String JIRA_ID_PATTERN = "([A-Z]+-[0-9]+)";

	private static final List<String> IGNORE_FILES = new ArrayList<>();
	
	static{
		IGNORE_FILES.add( "pom.xml" );
	}
	
    public static void main( String[] args ) throws IOException
    {
    	File workingDir = new File( WORKING_DIR );
    	File repoDir = resolveRepoDir( workingDir );
    	File projectRoot = repoDir.getParentFile();
    	
    	String projectRootPath = projectRoot.getAbsolutePath();
    	projectRootPath = projectRootPath.replaceAll( "\\\\", "/" );
    	
    	String mavenModulePath = workingDir.getAbsolutePath();
    	mavenModulePath = mavenModulePath.replaceAll( "\\\\", "/" ).replaceAll( projectRootPath, "" );
    	mavenModulePath = mavenModulePath.substring( 1 );
    	
		App app1 = new App( repoDir.getAbsolutePath(), mavenModulePath, IGNORE_FILES, START_REF, LOWEST_REF, JIRA_ID_PATTERN );
//		App app2 = new App( repoDir.getAbsolutePath(), mavenModulePath, IGNORE_FILES, MIDDLE_REF, LOWEST_REF, JIRA_ID_PATTERN );
		
		GitDeltaSummary summary1 = app1.call();
//		GitDeltaSummary summary2 = app2.call();
//		GitDeltaSummary blank = new GitDeltaSummary();
		
		ObjectMapper mapper = new ObjectMapper();
		
		StringWriter writer1 = new StringWriter();
		mapper.writerWithDefaultPrettyPrinter().writeValue( writer1, summary1 );
		String json1 = writer1.toString();
//		
//		StringWriter writer2 = new StringWriter();
//		mapper.writerWithDefaultPrettyPrinter().writeValue( writer2, summary2 );
//		String json2 = writer2.toString();
//		
//		StringWriter writer3 = new StringWriter();
//		mapper.writerWithDefaultPrettyPrinter().writeValue( writer3, blank );
//		String json3 = writer3.toString();
		
		System.out.println( json1 );
//		System.out.println( json2 );
//		System.out.println( json3 );
	}
    
    private static File resolveRepoDir( File workingDir ) throws IOException
    {
    	File repoDir = new File( workingDir.getAbsolutePath() );
    	while( repoDir != null )
    	{
    		repoDir = repoDir.getParentFile();
    		
    		File possibleMatch = new File( repoDir, ".git" );
    		
    		if( possibleMatch.exists() )
    		{
    			repoDir = possibleMatch;
    			break;
    		}
    	}
    	
    	if( repoDir == null || !repoDir.exists() )
    	{
    		throw new IOException( "Could not locate repository directory" );
    	}
    	
    	return repoDir;
    }
    
    private final String       repoPath;
    private final String       start;
    private final String       end;
    private final String       regex;
    private final String       mavenModulePath;
    private final List<String> ignoreFiles;
    
    public App( String repoPath, String mavenModulePath, List<String> ignoreFiles, String start, String end, String regex )
    {
    	this.repoPath        = repoPath;
    	this.start           = start;
    	this.end             = end;
    	this.regex           = regex;
    	this.mavenModulePath = mavenModulePath;
    	this.ignoreFiles     = ignoreFiles;
	}
    
    @Override
    public GitDeltaSummary call() throws IOException 
    {
		Repository repo = new FileRepositoryBuilder()
			.setGitDir( new File( this.repoPath ) )
			.readEnvironment()
			.findGitDir()
			.build();
		
    	GitDeltaSummaryGenerator generator = new GitDeltaSummaryGenerator( repo, mavenModulePath, ignoreFiles );
    	
    	return generator.generate( this.start, this.end, this.regex );
    }
}
