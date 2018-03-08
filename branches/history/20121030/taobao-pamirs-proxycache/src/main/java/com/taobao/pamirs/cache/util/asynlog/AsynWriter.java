package com.taobao.pamirs.cache.util.asynlog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * �첽��־����
 * 
 * @author xiaocheng 2012-11-9
 */
public class AsynWriter<T> implements IWriter<T> {

	private static final Log log = LogFactory.getLog(AsynWriter.class);

	/**
	 * ��־����
	 */
	private BlockingQueue<T> logQueue;

	/**
	 * д�̳߳�
	 */
	private ExecutorService asynWriterService;

	/**
	 * ����
	 */
	private LogConfig config;

	private List<WriterTask<T>> tasks = new ArrayList<WriterTask<T>>();

	public AsynWriter() {
		this(null);
	}

	public AsynWriter(LogConfig logConfig) {
		if (logConfig == null)
			config = new LogConfig();
		else
			config = logConfig;

		logQueue = new LinkedBlockingQueue<T>();
		asynWriterService = Executors.newFixedThreadPool(config
				.getAsynWriterThreadSize());
		for (int i = 0; i < config.getAsynWriterThreadSize(); i++) {
			WriterTask<T> task = new WriterTask<T>();
			task.setConfig(config);
			task.setLogQueue(logQueue);
			tasks.add(task);
			asynWriterService.submit(task);
		}

		log.info("Asyn log init ok!");
	}

	@Override
	public void write(T content) {
		if (content != null)
			logQueue.offer(content);
	}

	public void destroy() {
		logQueue.clear();

		for (WriterTask<T> task : tasks) {
			task.setActiveFlag(false);
		}

		asynWriterService.shutdown();
	}

}
