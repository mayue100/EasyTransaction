package com.mayue.trans.test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import com.mayue.trans.ConsistentGuardian;
import com.mayue.trans.config.EasyTransConifg;
import com.mayue.trans.log.TransactionLogReader;
import com.mayue.trans.log.vo.LogCollection;
import com.mayue.trans.protocol.BusinessIdentifer;
import com.mayue.trans.protocol.IdempotentTypeDeclare;
import com.mayue.trans.rpc.EasyTransRpcConsumer;
import com.mayue.trans.test.mockservice.accounting.AccountingService;
import com.mayue.trans.test.mockservice.accounting.easytrans.AccountingCpsMethod;
import com.mayue.trans.test.mockservice.express.ExpressService;
import com.mayue.trans.test.mockservice.express.easytrans.ExpressDeliverAfterTransMethod;
import com.mayue.trans.test.mockservice.order.NotReliableOrderMessage;
import com.mayue.trans.test.mockservice.order.OrderService;
import com.mayue.trans.test.mockservice.point.PointService;
import com.mayue.trans.test.mockservice.wallet.WalletService;
import com.mayue.trans.test.mockservice.wallet.easytrans.WalletPayTccMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.mayue.trans.test.mockservice.order.OrderMessage;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:test-ApplicationContext.xml" })
public class FullTest extends AbstractJUnit4SpringContextTests {
	
	
	@Resource(name="wholeJdbcTemplate")
	private JdbcTemplate wholeJdbcTemplate;
	
	@Resource
    OrderService orderService;
	
	@Resource
	private ConsistentGuardian guardian;
	
	@Resource
	private TransactionLogReader logReader;
	
	@Resource
	private EasyTransConifg config;
	
	@Resource
	private EasyTransRpcConsumer consumer;
	
	@Resource
	private AccountingService accountingService;
	@Resource
	private ExpressService expressService;
	@Resource
	private PointService pointService;
	@Resource
	private WalletService walletService;
	
	private ExecutorService executor = Executors.newFixedThreadPool(4);
//	private ExecutorService executor = Executors.newFixedThreadPool(1);
	
	private Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	private int concurrentTestId = 100000;
	
	@Test
	public void test(){

		try {
			//synchronizer test
			cleanAndSetUp();
			commitedAndSubTransSuccess();
			rollbackWithExceptionJustBeforeCommit();
			rollbackWithExceptionInMiddle();
			rollbackWithExceptionJustAfterStartEasyTrans();
			
			//wait for asynchronous operation
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//consistent guardian test
			commitWithExceptionInMiddleOfConsistenGuardian();
			rollbackWithExceptionInMiddleOfConsistenGuardian();
			
			//idempotent test
			activateThreadPool();
			sameMethodConcurrentTcc();
			differentMethodConcurrentCompensable();
			
			//execute consistent guardian in case of timeout
			List<LogCollection> unfinishedLogs = logReader.getUnfinishedLogs(null, 100, new Date());
			for(LogCollection logCollection:unfinishedLogs){
				guardian.process(logCollection);
			}
			
			//check execute result
			Assert.isTrue(accountingService.getTotalCost(1) == 2000);
			Assert.isTrue(expressService.getUserExpressCount(1) == 2);
			Assert.isTrue(orderService.getUserOrderCount(1) == 2);
			Assert.isTrue(pointService.getUserPoint(1) == 2000);
			Assert.isTrue(walletService.getUserTotalAmount(1) == 8000);
			Assert.isTrue(walletService.getUserFreezeAmount(1) == 0);
			
			
			//check single
			executeTccOnly();
			executeCompensableOnly();
			executeAfterTransMethodOnly();
			executeReliableMsgOnly();
			executeNotReliableMessageOnly();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Assert.isTrue(walletService.getUserTotalAmount(1) == 7000);
			Assert.isTrue(walletService.getUserFreezeAmount(1) == 0);
			Assert.isTrue(accountingService.getTotalCost(1) == 3000);
			Assert.isTrue(expressService.getUserExpressCount(1) == 3);
			Assert.isTrue(pointService.getUserPoint(1) == 3000);
			
			System.out.println("Test Passed!");
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Finish!");
	}
	
	private void executeNotReliableMessageOnly() {
		OrderService.setNotExecuteBusiness(AccountingCpsMethod.AccountingRequest.class);
		OrderService.setNotExecuteBusiness(ExpressDeliverAfterTransMethod.ExpressDeliverAfterTransMethodRequest.class);
//		OrderService.setNotExecuteBusiness(NotReliableOrderMessage.class);
		OrderService.setNotExecuteBusiness(OrderMessage.class);
		OrderService.setNotExecuteBusiness(WalletPayTccMethod.WalletPayTccMethodRequest.class);
		orderService.buySomething(1, 1000);
		OrderService.clearNotExecuteSet();
	}
	
	private void executeAfterTransMethodOnly() {
		OrderService.setNotExecuteBusiness(AccountingCpsMethod.AccountingRequest.class);
//		OrderService.setNotExecuteBusiness(ExpressDeliverAfterTransMethodRequest.class);
		OrderService.setNotExecuteBusiness(NotReliableOrderMessage.class);
		OrderService.setNotExecuteBusiness(OrderMessage.class);
		OrderService.setNotExecuteBusiness(WalletPayTccMethod.WalletPayTccMethodRequest.class);
		orderService.buySomething(1, 1000);
		OrderService.clearNotExecuteSet();
	}
	
	private void executeReliableMsgOnly() {
		OrderService.setNotExecuteBusiness(AccountingCpsMethod.AccountingRequest.class);
		OrderService.setNotExecuteBusiness(ExpressDeliverAfterTransMethod.ExpressDeliverAfterTransMethodRequest.class);
		OrderService.setNotExecuteBusiness(NotReliableOrderMessage.class);
//		OrderService.setNotExecuteBusiness(OrderMessage.class);
		OrderService.setNotExecuteBusiness(WalletPayTccMethod.WalletPayTccMethodRequest.class);
		orderService.buySomething(1, 1000);
		OrderService.clearNotExecuteSet();
	}

	private void executeTccOnly() {
		OrderService.setNotExecuteBusiness(AccountingCpsMethod.AccountingRequest.class);
		OrderService.setNotExecuteBusiness(ExpressDeliverAfterTransMethod.ExpressDeliverAfterTransMethodRequest.class);
		OrderService.setNotExecuteBusiness(NotReliableOrderMessage.class);
		OrderService.setNotExecuteBusiness(OrderMessage.class);
//		OrderService.setNotExecuteBusiness(WalletPayTccMethodRequest.class);
		orderService.buySomething(1, 1000);
		OrderService.clearNotExecuteSet();
	}
	
	private void executeCompensableOnly() {
//		OrderService.setNotExecuteBusiness(AccountingRequest.class);
		OrderService.setNotExecuteBusiness(ExpressDeliverAfterTransMethod.ExpressDeliverAfterTransMethodRequest.class);
		OrderService.setNotExecuteBusiness(NotReliableOrderMessage.class);
		OrderService.setNotExecuteBusiness(OrderMessage.class);
		OrderService.setNotExecuteBusiness(WalletPayTccMethod.WalletPayTccMethodRequest.class);
		orderService.buySomething(1, 1000);
		OrderService.clearNotExecuteSet();
	}
	
	private void differentMethodConcurrentCompensable() {

		final BusinessIdentifer annotation = AccountingCpsMethod.AccountingRequest.class.getAnnotation(BusinessIdentifer.class);
		final int i = concurrentTestId++;
		
		final AccountingCpsMethod.AccountingRequest request = new AccountingCpsMethod.AccountingRequest();
		request.setAmount(1000l);
		request.setUserId(1);
		IdempotentTypeDeclare.TransactionId parentTrxId = new IdempotentTypeDeclare.TransactionId(config.getAppId(), "concurrentTest", String.valueOf(i));
		request.setParentTrxId(parentTrxId);
		
		Callable<Object> doCompensableBusinessRequest = new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				return consumer.call(annotation.appId(), annotation.busCode(), "doCompensableBusiness", request);
			}
		};
		
		Callable<Object> compensationRequest = new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				return consumer.call(annotation.appId(), annotation.busCode(), "compensation", request);
			}
		};
		
		List<Callable<Object>> asListTry = Arrays.asList(compensationRequest,doCompensableBusinessRequest,compensationRequest,doCompensableBusinessRequest,compensationRequest,doCompensableBusinessRequest,compensationRequest,doCompensableBusinessRequest);
		try {
			List<Future<Object>> invokeAll = executor.invokeAll(asListTry);
			for(Future<Object> future:invokeAll){
				try {
					System.out.println(future.get());
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void activateThreadPool() {
		Callable<Object> runnable = new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				return null;
			}
		};
		
		try {
			executor.invokeAll(Arrays.asList(runnable,runnable,runnable,runnable,runnable,runnable,runnable,runnable));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void sameMethodConcurrentTcc() {
		final BusinessIdentifer annotation = WalletPayTccMethod.WalletPayTccMethodRequest.class.getAnnotation(BusinessIdentifer.class);
		final int i = concurrentTestId++;
		
		final WalletPayTccMethod.WalletPayTccMethodRequest request = new WalletPayTccMethod.WalletPayTccMethodRequest();
		request.setPayAmount(1000l);
		request.setUserId(1);
		IdempotentTypeDeclare.TransactionId parentTrxId = new IdempotentTypeDeclare.TransactionId(config.getAppId(), "concurrentTest", String.valueOf(i));
		request.setParentTrxId(parentTrxId);
		
		Callable<Object> tryRequest = new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				return consumer.call(annotation.appId(), annotation.busCode(), "doTry", request);
			}
		};
		
		Callable<Object> cancelRequest = new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				return consumer.call(annotation.appId(), annotation.busCode(), "doCancel", request);
			}
		};
		
		
		List<Callable<Object>> asListTry = Arrays.asList(tryRequest,tryRequest,tryRequest,tryRequest,tryRequest,tryRequest,tryRequest,tryRequest);
		try {
			List<Future<Object>> invokeAll = executor.invokeAll(asListTry);
			for(Future<Object> future:invokeAll){
				try {
					System.out.println(future.get());
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		List<Callable<Object>> asListCancel = Arrays.asList(cancelRequest,cancelRequest,cancelRequest,cancelRequest,cancelRequest,cancelRequest,cancelRequest,cancelRequest);
		try {
			List<Future<Object>> invokeAll = executor.invokeAll(asListCancel);
			for(Future<Object> future:invokeAll){
				try {
					System.out.println(future.get());
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	private void rollbackWithExceptionInMiddleOfConsistenGuardian() {
		try {
			OrderService.setExceptionTag(OrderService.EXCEPTION_TAG_BEFORE_COMMIT);
			OrderService.setExceptionTag(OrderService.EXCEPTION_TAG_IN_MIDDLE_OF_CONSISTENT_GUARDIAN_WITH_ROLLEDBACK_MASTER_TRANS);
			orderService.buySomething(1, 1000);
		} catch (Exception e) {
			LOG.info("",e);
		}
		
		try {
			Thread.sleep(1000);//wait for asynchronous operation
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		OrderService.clearExceptionSet();
		List<LogCollection> unfinishedLogs = logReader.getUnfinishedLogs(null, 1, new Date());
		LogCollection logCollection = unfinishedLogs.get(0);
		guardian.process(logCollection);
	}

	private void commitWithExceptionInMiddleOfConsistenGuardian() {
		try {
			OrderService.setExceptionTag(OrderService.EXCEPTION_TAG_IN_MIDDLE_OF_CONSISTENT_GUARDIAN_WITH_SUCCESS_MASTER_TRANS);
			orderService.buySomething(1, 1000);
		} catch (Exception e) {
			LOG.info("",e);
		}
		
		try {
			Thread.sleep(1000);//wait for asynchronous operation
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		OrderService.clearExceptionSet();
		List<LogCollection> unfinishedLogs = logReader.getUnfinishedLogs(null, 1, new Date());
		LogCollection logCollection = unfinishedLogs.get(0);
		guardian.process(logCollection);
	}

	private void cleanAndSetUp() {
		wholeJdbcTemplate.batchUpdate(new String[]{
				"Create Table If Not Exists `order` (  `order_id` int(11) NOT NULL AUTO_INCREMENT,  `user_id` int(11) NOT NULL,  `money` bigint(20) NOT NULL,  `create_time` datetime NOT NULL,  PRIMARY KEY (`order_id`)) DEFAULT CHARSET=utf8",
				"TRUNCATE `order`",
				"Create Table If Not Exists `wallet` (  `user_id` int(11) NOT NULL,  `total_amount` bigint(20) NOT NULL,  `freeze_amount` bigint(20) NOT NULL,  PRIMARY KEY (`user_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8",
				"TRUNCATE `wallet`",
				"Create Table If Not Exists  `accounting` (  `accounting_id` int(11) NOT NULL AUTO_INCREMENT,  `p_app_id` varchar(32) NOT NULL,  `p_bus_code` varchar(128) NOT NULL,  `p_trx_id` varchar(64) NOT NULL,  `user_id` int(11) NOT NULL,  `amount` bigint(20) NOT NULL,  `create_time` datetime NOT NULL,  PRIMARY KEY (`accounting_id`)) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8",
				"TRUNCATE `accounting`",
				"Create Table If Not Exists `point` (  `user_id` int(11) NOT NULL,  `point` bigint(20) NOT NULL,  PRIMARY KEY (`user_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8",
				"TRUNCATE `point`",
				"Create Table If Not Exists `express` (  `p_app_id` varchar(32) NOT NULL,  `p_bus_code` varchar(128) NOT NULL,  `p_trx_id` varchar(64) NOT NULL,  `user_id` int(11) NOT NULL,  PRIMARY KEY (`p_app_id`,`p_bus_code`,`p_trx_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8",
				"TRUNCATE `express`",
				"TRUNCATE `trans_log_unfinished`",
				"TRUNCATE `trans_log_detail`",
				"TRUNCATE `executed_trans`",
				"TRUNCATE `idempotent`",
				"INSERT INTO `wallet` (`user_id`, `total_amount`, `freeze_amount`) VALUES ('1', '10000', '0')",
				"INSERT INTO `point` (`user_id`, `point`) VALUES ('1', '0')"
		});
	}

	public void commitedAndSubTransSuccess(){
		orderService.buySomething(1, 1000);
	}
	
	public void rollbackWithExceptionJustBeforeCommit(){
		try {
			OrderService.setExceptionTag(OrderService.EXCEPTION_TAG_BEFORE_COMMIT);
			orderService.buySomething(1, 1000);
		} catch (Exception e) {
			LOG.info("",e);
		}
		OrderService.clearExceptionSet();
	}
	
	public void rollbackWithExceptionInMiddle(){
		try {
			OrderService.setExceptionTag(OrderService.EXCEPTION_TAG_IN_THE_MIDDLE);
			orderService.buySomething(1, 1000);
		} catch (Exception e) {
			LOG.info("",e);
		}
		OrderService.clearExceptionSet();
	}
	
	public void rollbackWithExceptionJustAfterStartEasyTrans(){
		try {
			OrderService.setExceptionTag(OrderService.EXCEPTION_TAG_JUST_AFTER_START_EASY_TRANSACTION);
			orderService.buySomething(1, 1000);
		} catch (Exception e) {
			LOG.info("",e);
		}
		OrderService.clearExceptionSet();
	}
	
}
