package net.tmclean.git.miner.walker;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;

public interface GitWalkListener 
{
	void onCommit( RevCommit commit, List<String> refAliases );
}
