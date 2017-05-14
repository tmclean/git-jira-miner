package net.tmclean.git.miner;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

import net.tmclean.git.miner.diff.GitEntityAffectedPathsResolver;
import net.tmclean.git.miner.walker.GitMetadataGrepWalkListener;
import net.tmclean.git.miner.walker.GitRepoWalker;

public class GitDeltaSummaryGenerator 
{
	private final Repository repo;
	private final String mavenModulePath;
	private final List<String> ignoreFiles;
	
	public GitDeltaSummaryGenerator( Repository repo, String mavenModulePath, List<String> ignoreFiles )
	{
		this.repo = repo;
		this.mavenModulePath = mavenModulePath;
		this.ignoreFiles = ignoreFiles;
	}
	
	public GitDeltaSummary generate( String start, String end, String regex ) throws IOException
	{
		Ref startRef = repo.findRef( start );
		String startId = startRef.getLeaf().getObjectId().name();
		
		Ref endRef = repo.findRef( end );
		String endId = endRef.getLeaf().getObjectId().name();
		
		GitDeltaSummary summary = new GitDeltaSummary();
		summary.setStart( new GrepRef( startRef.getName(), startId ) );
		summary.setEnd( new GrepRef( endRef.getName(), endId ) );

    	GitEntityAffectedPathsResolver affectedFilesResolver = new GitEntityAffectedPathsResolver();
		List<String> affectedPaths = affectedFilesResolver.resolve( repo, mavenModulePath, ignoreFiles, start, end );
		summary.setAffectedPaths( affectedPaths );
		
		// Only hunt for grep for matches if the revs contain files in the current module
		if( !affectedPaths.isEmpty() )
		{
			Pattern pattern = Pattern.compile( regex );
	    	
	    	GitMetadataGrepWalkListener grepListener = new GitMetadataGrepWalkListener( pattern );
	    	GitRepoWalker walker = new GitRepoWalker( repo );
	    	walker.walk( start, end, grepListener );
	    	    	
	    	Map<String, GitMatchedEntity> matchedEntities = grepListener.getMatches();
			summary.setMatchedEntities( matchedEntities );
		}
		
		return summary;
	}
}
