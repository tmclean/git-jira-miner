package net.tmclean.git.miner.main;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.Callable;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.tmclean.git.miner.GitDeltaSummary;
import net.tmclean.git.miner.GitDeltaSummaryGenerator;

public class App implements Callable<GitDeltaSummary>
{
	private static final String REPO_DIR = "C:/Users/Tom McLean/Desktop/fdd-data-api/.git";
	private static final String START_REF = "develop";
	private static final String MIDDLE_REF = "master";
	private static final String LOWEST_REF = "9.2.7.6";
	private static final String JIRA_ID_PATTERN = "([A-Z]+-[0-9]+)";
	
    public static void main( String[] args ) throws IOException
    {
		App app1 = new App( REPO_DIR, START_REF, MIDDLE_REF, JIRA_ID_PATTERN );
		App app2 = new App( REPO_DIR, MIDDLE_REF, LOWEST_REF, JIRA_ID_PATTERN );
		
		GitDeltaSummary summary1 = app1.call();
		GitDeltaSummary summary2 = app2.call();
		GitDeltaSummary blank = new GitDeltaSummary();
		
		ObjectMapper mapper = new ObjectMapper();
		
		StringWriter writer1 = new StringWriter();
		mapper.writerWithDefaultPrettyPrinter().writeValue( writer1, summary1 );
		String json1 = writer1.toString();
		
		StringWriter writer2 = new StringWriter();
		mapper.writerWithDefaultPrettyPrinter().writeValue( writer2, summary2 );
		String json2 = writer2.toString();
		
		StringWriter writer3 = new StringWriter();
		mapper.writerWithDefaultPrettyPrinter().writeValue( writer3, blank );
		String json3 = writer3.toString();
		
		System.out.println( json1 );
		System.out.println( json2 );
		System.out.println( json3 );
	}
    
    private final String repoPath;
    private final String start;
    private final String end;
    private final String regex;
    
    public App( String repoPath, String start, String end, String regex )
    {
    	this.repoPath = repoPath;
    	this.start    = start;
    	this.end      = end;
    	this.regex    = regex;
	}
    
    @Override
    public GitDeltaSummary call() throws IOException 
    {
		Repository repo = new FileRepositoryBuilder()
			.setGitDir( new File( this.repoPath ) )
			.readEnvironment()
			.findGitDir()
			.build();
		
    	GitDeltaSummaryGenerator generator = new GitDeltaSummaryGenerator( repo );
    	
    	return generator.generate( this.start, this.end, this.regex );
    }
}
