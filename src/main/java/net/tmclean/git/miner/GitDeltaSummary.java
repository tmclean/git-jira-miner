package net.tmclean.git.miner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitDeltaSummary 
{
	private GrepRef start;
	private GrepRef end;
	
	private Map<String, GitMatchedEntity> matchedEntities = new HashMap<>();
	private List<String> affectedPaths = new ArrayList<>();

	public GrepRef getStart() { return start; }
	public void setStart(GrepRef start) { this.start = start; }
	
	public GrepRef getEnd() { return end; }
	public void setEnd(GrepRef end) { this.end = end; }
	
	public Map<String, GitMatchedEntity> getMatchedEntities() { return matchedEntities; }
	public void setMatchedEntities(Map<String, GitMatchedEntity> matchedEntities) { this.matchedEntities = matchedEntities; }
	
	public List<String> getAffectedPaths() { return affectedPaths; }
	public void setAffectedPaths(List<String> affectedPaths) { this.affectedPaths = affectedPaths; }
}
