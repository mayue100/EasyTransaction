package com.mayue.trans.log.vo;

import java.io.Serializable;
import java.util.HashMap;

import com.mayue.trans.log.vo.aft.AfterTransCalledContent;
import com.mayue.trans.log.vo.compensable.PreCompensableCallContent;
import com.mayue.trans.log.vo.tcc.TccCallCancelledContent;
import com.mayue.trans.LogProcessor;
import com.mayue.trans.executor.AfterTransMethodExecutor;
import com.mayue.trans.executor.CompensableMethodExecutor;
import com.mayue.trans.executor.ReliableMessageMethodExecutor;
import com.mayue.trans.executor.TccMethodExecutor;
import com.mayue.trans.log.vo.aft.AfterTransCallRegisterContent;
import com.mayue.trans.log.vo.compensable.CompensatedContent;
import com.mayue.trans.log.vo.msg.MessageRecordContent;
import com.mayue.trans.log.vo.msg.MessageSentContent;
import com.mayue.trans.log.vo.tcc.PreTccCallContent;
import com.mayue.trans.log.vo.tcc.TccCallConfirmedContent;


/**
 * 单条日志里的实质内容列表项
 * @author THINK
 *
 */
public abstract class Content implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * the unique contentId in a transaction.
	 * @return
	 */
	private Integer cId;
	
	public abstract int getLogType();
	
	public Integer getcId() {
		return cId;
	}

	public void setcId(Integer contentId) {
		this.cId = contentId;
	}



	public static enum ContentType{
		TransactionBegin(1,null,null),
		TransactionFininshed(2,null,null),
		PreTccCall(3,TccMethodExecutor.class,PreTccCallContent.class),
		TccCallConfirmed(4,null,TccCallConfirmedContent.class),
		TccCallCanceled(5,null,TccCallCancelledContent.class),
		MessageRecord(6,ReliableMessageMethodExecutor.class,MessageRecordContent.class),
		MessageSent(7,null,MessageSentContent.class),
		PreCompensableCall(8,CompensableMethodExecutor.class,PreCompensableCallContent.class),
		Compensated(9,null,CompensatedContent.class),
		AfterTransCallRegister(10,AfterTransMethodExecutor.class,AfterTransCallRegisterContent.class),
		AfterTransCalled(11,null,AfterTransCalledContent.class)
		;
		
		private static HashMap<Integer,ContentType> map = new HashMap<Integer, Content.ContentType>();
		static{
			for(ContentType type :ContentType.values()){
				map.put(type.getContentTypeId(), type);
			}
		}
		
		private final int contentTypeId;
		private final Class<? extends LogProcessor> proccessorClass;
		private Class<? extends Content> correspondingClass;
		
		public int getContentTypeId() {
			return contentTypeId;
		}
		
		public Class<? extends LogProcessor> getProccessorClass() {
			return proccessorClass;
		}
		
		public Class<? extends Content> getCorrespondingClass() {
			return correspondingClass;
		}
		
		public static ContentType getById(int id){
			return map.get(id);
		}

		ContentType(int id,Class<? extends LogProcessor> proccessorClass,Class<? extends Content> correspondingClass){
			this.contentTypeId = id;
			this.proccessorClass = proccessorClass;
			this.correspondingClass = correspondingClass;
		}
	}
}
