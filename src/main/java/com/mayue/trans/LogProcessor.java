package com.mayue.trans;

import com.mayue.trans.context.LogProcessContext;
import com.mayue.trans.log.vo.Content;

/**
 * the processor for each log
 */
public interface LogProcessor {
	
	/**
	 * the process method for specified log type
	 * @param ctx log processing context 
	 * @param currentContent processing content
	 * @return true for success,false for end processing and retry later
	 */
	boolean logProcess(LogProcessContext ctx, Content currentContent);
}
