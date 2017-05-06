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
	
	public GitDeltaSummaryGenerator( Repository repo )
	{
		this.repo = repo;
	}
	
	public GitDeltaSummary generate( String start, String end, String regex ) throws IOException
	{
    	Pattern pattern = Pattern.compile( regex );
    	
    	GitMetadataGrepWalkListener grepListener = new GitMetadataGrepWalkListener( pattern );
    	GitRepoWalker walker = new GitRepoWalker( repo );
    	walker.walk( start, end, grepListener );
    	
    	GitEntityAffectedPathsResolver affectedFilesResolver = new GitEntityAffectedPathsResolver();
    	
    	Map<String, GitMatchedEntity> matchedEntities = grepListener.getMatches();
    	
		List<String> affectedPaths = affectedFilesResolver.resolve( repo, start, end );
		
		Ref startRef = repo.findRef( start );
		String startId = startRef.getLeaf().getObjectId().name();
		
		Ref endRef = repo.findRef( end );
		String endId = endRef.getLeaf().getObjectId().name();
		
		GitDeltaSummary summary = new GitDeltaSummary();
		summary.setStart( new GrepRef( startRef.getName(), startId ) );
		summary.setEnd( new GrepRef( endRef.getName(), endId ) );
		summary.setMatchedEntities( matchedEntities );
		summary.setAffectedPaths( affectedPaths );
		
		return summary;
	}
}
