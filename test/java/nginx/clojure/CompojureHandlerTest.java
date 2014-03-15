package nginx.clojure;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import clojure.lang.AFn;
import clojure.lang.IFn;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CompojureHandlerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

//	@Test
//	public void testSimpleHandler1() {
//		RT.var("clojure.core", "require").invoke(Symbol.create("nginx.clojure.compojure-fns-for-test"));
////		System.out.println("rq cl :" + rq.getClass().getClassLoader());
//		final IFn  fn = (IFn)RT.var("nginx.clojure.compojure-fns-for-test", "simple-handler").fn();
//		for (int i = 0; i < 2; i++) {
//			final Map<String,Object> resp = new HashMap<String, Object>();
//			System.out.println("test at i=" + i);
//			Coroutine cr = new Coroutine(new Runnable() {
//				@Override
//				public void run() throws SuspendExecution {
//					resp.putAll((Map)fn.invoke());
//				}
//			});
//			cr.resume();
//			Assert.assertEquals(0, resp.size());
//			System.out.println("before resume");
//			cr.resume();
//			Assert.assertEquals(3, resp.size());
//			Assert.assertEquals("Simple Response\n", resp.get(RT.keyword(null, "body")));
//			Assert.assertEquals(Coroutine.State.FINISHED, cr.getState());
////			Stack st = Stack.getStack();
////			System.out.println(st.curMethodSP);
//		}
//	}
	
//	@Test
//	public void testSimpleHandler11() {
//		System.out.println("do test testSimpleHandler11");
//		RT.var("clojure.core", "require").invoke(Symbol.create("nginx.clojure.compojure-fns-for-test"));
////		System.out.println("rq cl :" + rq.getClass().getClassLoader());
//		final IFn  fn = (IFn)RT.var("nginx.clojure.compojure-fns-for-test", "simple-handler").fn();
//		final Map<String,Object> resp = new HashMap<String, Object>();
//		Coroutine cr = new Coroutine(new Runnable() {
//			@Override
//			public void run() throws SuspendExecution {
//				resp.putAll((Map)fn.invoke());
//			}
//		});
//		cr.resume();
//		Assert.assertEquals(0, resp.size());
//		System.out.println("before resume");
//		cr.resume();
//		Assert.assertEquals(3, resp.size());
//		Assert.assertEquals("Simple Response\n", resp.get(RT.keyword(null, "body")));
//	}
	
	public static final class MyRunner implements Runnable {
		Object request;
		Map response = new HashMap();
		final IFn handler;
		
		
		public MyRunner(IFn handler, Object request) {
			super();
			if (handler instanceof Var) {
				handler = ((Var)handler).fn();
			}
			this.handler = handler;
			this.request = request;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void run() throws SuspendExecution {
			try {
				response = (Map) handler.invoke(request);
			}catch(Throwable e) {
				System.err.println("in coroutine run handler error");
				e.printStackTrace();
			}
		}
	}
	
//	@Test
//	public void testSimpleHandler2() {
//		RT.var("clojure.core", "require").invoke(Symbol.create("nginx.clojure.compojure-fns-for-test"));
////		System.out.println("rq cl :" + rq.getClass().getClassLoader());
//		
//		IFn  fn = (IFn)RT.var("nginx.clojure.compojure-fns-for-test", "simple-selfresume-handler").fn();
//		MyRunner myrunner = new MyRunner(fn, "just a test");
//		Coroutine cr = new Coroutine(myrunner);
//		cr.resume();
//		Assert.assertEquals(0, myrunner.response.size());
//		try {
//			Thread.sleep(4000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Assert.assertEquals(3, myrunner.response.size());
//		Assert.assertEquals("Simple Response\n", myrunner.response.get(RT.keyword(null, "body")));
//	}

	@Test
	public void testSimpleHandler3() {
		RT.var("clojure.core", "require").invoke(Symbol.create("nginx.clojure.coroutine-socket-handlers-for-test"));
//		System.out.println("rq cl :" + rq.getClass().getClassLoader());
		
//		IFn  fn = (IFn)RT.var("nginx.clojure.coroutine-socket-handlers-for-test", "coroutine-socket-test-handler").fn();
		String code = "(do " +
             "(use \'[compojure.core])" + 
             "(use \'[nginx.clojure.coroutine-socket-handlers-for-test])" + 
             "(context \"/coroutineSocketAndCompojure\" [] coroutine-socket-test-handler)" +
//             "coroutine-socket-test-handler" +
           ")";
		IFn fn = (IFn)RT.var("clojure.core", "eval").invoke(RT.var("clojure.core","read-string").invoke(code));
		//{:uri "/simple", :scheme :http, :request-method :get, :headers {}}
		PersistentArrayMap request = new PersistentArrayMap(new Object[] {
				RT.keyword(null, "uri"), "/coroutineSocketAndCompojure/simple"
//				RT.keyword(null, "uri"), "/simple"
			    ,RT.keyword(null, "scheme"), RT.keyword(null, "http")
			    ,RT.keyword(null, "request-method"), RT.keyword(null, "get")
			    ,RT.keyword(null, "headers"), PersistentArrayMap.EMPTY
		});
		
		MyRunner myrunner = new MyRunner(fn, request);
		Coroutine cr = new Coroutine(myrunner);
		cr.resume();
		Assert.assertEquals(0, myrunner.response.size());
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertEquals(3, myrunner.response.size());
		Assert.assertEquals("Simple Response\n", myrunner.response.get(RT.keyword(null, "body")));
	}
	
//	@Test
//	public void testSimpleHandler30() {
//
//		final MyRunner[] ref = new MyRunner[1];
//
//		Runnable runnable = new Runnable() {
//			
//			@Override
//			public void run() throws SuspendExecution {
//				RT.var("clojure.core", "require").invoke(Symbol.create("nginx.clojure.coroutine-socket-handlers-for-test"));
////				System.out.println("rq cl :" + rq.getClass().getClassLoader());
//				
////				IFn  fn = (IFn)RT.var("nginx.clojure.coroutine-socket-handlers-for-test", "coroutine-socket-test-handler").fn();
//				String code = "(do " +
//		             "(use \'[compojure.core])" + 
//		             "(use \'[nginx.clojure.coroutine-socket-handlers-for-test])" + 
//		             "(context \"/coroutineSocketAndCompojure\" [] coroutine-socket-test-handler)" +
////		             "coroutine-socket-test-handler" +
//		           ")";
//				IFn fn = (IFn)RT.var("clojure.core", "eval").invoke(RT.var("clojure.core","read-string").invoke(code));
//				//{:uri "/simple", :scheme :http, :request-method :get, :headers {}}
//				PersistentArrayMap request = new PersistentArrayMap(new Object[] {
//						RT.keyword(null, "uri"), "/coroutineSocketAndCompojure/simple"
////						RT.keyword(null, "uri"), "/simple"
//					    ,RT.keyword(null, "scheme"), RT.keyword(null, "http")
//					    ,RT.keyword(null, "request-method"), RT.keyword(null, "get")
//					    ,RT.keyword(null, "headers"), PersistentArrayMap.EMPTY
//				});
//				MyRunner myRunner = ref[0] = new MyRunner(fn, request);
//				myRunner.run();
//				System.out.println("end of run");
//			}
//		};
//		
//		MyRunner myrunner = ref[0];
//		Coroutine cr = new Coroutine(runnable);
//		cr.resume();
//		Assert.assertEquals(0, myrunner.response.size());
//		try {
//			Thread.sleep(4000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Assert.assertEquals(3, myrunner.response.size());
//		Assert.assertEquals("Simple Response\n", myrunner.response.get(RT.keyword(null, "body")));
//	}
	
	
//	@Test
//	public void testSimpleHandler31() {
//		RT.var("clojure.core", "require").invoke(Symbol.create("nginx.clojure.coroutine-socket-handlers-for-test"));
////		System.out.println("rq cl :" + rq.getClass().getClassLoader());
//		
////		IFn  fn = (IFn)RT.var("nginx.clojure.coroutine-socket-handlers-for-test", "coroutine-socket-test-handler").fn();
//		String code = "(do " +
//             "(use \'[compojure.core])" + 
//             "(use \'[nginx.clojure.coroutine-socket-handlers-for-test])" + 
////             "(context \"/coroutineSocketAndCompojure\" [] coroutine-socket-test-handler)" +
//             "coroutine-socket-test-handler" +
//           ")";
//		IFn fn = (IFn)RT.var("clojure.core", "eval").invoke(RT.var("clojure.core","read-string").invoke(code));
//		//{:uri "/simple", :scheme :http, :request-method :get, :headers {}}
//		PersistentArrayMap request = new PersistentArrayMap(new Object[] {
////				RT.keyword(null, "uri"), "/coroutineSocketAndCompojure/simple"
//				RT.keyword(null, "uri"), "/simple"
//			    ,RT.keyword(null, "scheme"), RT.keyword(null, "http")
//			    ,RT.keyword(null, "request-method"), RT.keyword(null, "get")
//			    ,RT.keyword(null, "headers"), PersistentArrayMap.EMPTY
//		});
//		
//		MyRunner myrunner = new MyRunner(fn, request);
//		Coroutine cr = new Coroutine(myrunner);
//		cr.resume();
//		Assert.assertEquals(0, myrunner.response.size());
//		try {
//			Thread.sleep(4000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Assert.assertEquals(3, myrunner.response.size());
//		Assert.assertEquals("Simple Response\n", myrunner.response.get(RT.keyword(null, "body")));
//	}
//	
	
//	@Test
//	public void testSimpleHandler32() {
//		RT.var("clojure.core", "require").invoke(Symbol.create("nginx.clojure.coroutine-socket-handlers-for-test"));
////		System.out.println("rq cl :" + rq.getClass().getClassLoader());
//		
//		IFn  fn = (IFn)RT.var("nginx.clojure.coroutine-socket-handlers-for-test", "simple-handler").fn();
////		String code = "(do " +
////             "(use \'[compojure.core])" + 
////             "(use \'[nginx.clojure.coroutine-socket-handlers-for-test])" + 
//////             "(context \"/coroutineSocketAndCompojure\" [] coroutine-socket-test-handler)" +
////             "coroutine-socket-test-handler" +
////           ")";
////		IFn fn = (IFn)RT.var("clojure.core", "eval").invoke(RT.var("clojure.core","read-string").invoke(code));
//		//{:uri "/simple", :scheme :http, :request-method :get, :headers {}}
//		PersistentArrayMap request = new PersistentArrayMap(new Object[] {
////				RT.keyword(null, "uri"), "/coroutineSocketAndCompojure/simple"
//				RT.keyword(null, "uri"), "/simple2"
//			    ,RT.keyword(null, "scheme"), RT.keyword(null, "http")
//			    ,RT.keyword(null, "request-method"), RT.keyword(null, "get")
//			    ,RT.keyword(null, "headers"), PersistentArrayMap.EMPTY
//		});
//		
//		MyRunner myrunner = new MyRunner(fn, request);
//		Coroutine cr = new Coroutine(myrunner);
//		cr.resume();
//		Assert.assertEquals(0, myrunner.response.size());
//		cr.resume();
//		Assert.assertEquals(3, myrunner.response.size());
//		Assert.assertEquals("Simple Response\n", myrunner.response.get(RT.keyword(null, "body")));
//	}
	
	
//	@Test
//	public void testSimpleHandler4() {
//	
//		IFn fn = new AFn() {
//			@Override
//			public Object invoke(Object arg1) {
//				System.out.println("before");
//				final Coroutine cr = Coroutine.getActiveCoroutine();
//				new Thread() {
//					public void run() {
//						try {
//							Thread.sleep(2000);
//							cr.resume();
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//						
//					};
//				}.start();
//				Coroutine.yield();
//				System.out.println("after");
//				return new PersistentArrayMap(new Object[]{
//						RT.keyword(null, "body"), "Simple Response\n",
//						RT.keyword(null, "headers"), PersistentArrayMap.EMPTY,
//						RT.keyword(null, "status"), "200",
//				});
//			}
//		};
//		//{:uri "/simple", :scheme :http, :request-method :get, :headers {}}
//		PersistentArrayMap request = new PersistentArrayMap(new Object[] {
////				RT.keyword(null, "uri"), "/coroutineSocketAndCompojure/simple"
//				RT.keyword(null, "uri"), "/simple"
//			    ,RT.keyword(null, "scheme"), RT.keyword(null, "http")
//			    ,RT.keyword(null, "request-method"), RT.keyword(null, "get")
//			    ,RT.keyword(null, "headers"), PersistentArrayMap.EMPTY
//		});
//		
//		MyRunner myrunner = new MyRunner(fn, request);
//		Coroutine cr = new Coroutine(myrunner);
//		cr.resume();
//		Assert.assertEquals(0, myrunner.response.size());
//		try {
//			Thread.sleep(4000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Assert.assertEquals(3, myrunner.response.size());
//		Assert.assertEquals("Simple Response\n", myrunner.response.get(RT.keyword(null, "body")));
//	}
}