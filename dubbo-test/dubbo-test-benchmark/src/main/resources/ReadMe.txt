һ���½�һ��benchmark���̣���demo.benchmark
���������Լ�����Ľӿ�api����dubbo.benchmark.jar(��ѹdubbo.benchmark.tar.gz����libĿ¼��)
�����½�һ���࣬ʵ��AbstractClientRunnable
    a��ʵ�ָ���Ĺ��캯��
	b��ʵ��invoke������ͨ��serviceFactory�������ؽӿڴ�����ʵ���Լ���ҵ���߼�������
	 public Object invoke(ServiceFactory serviceFactory) {
        DemoService demoService = (DemoService) serviceFactory.get(DemoService.class);
        return demoService.sendRequest("hello");
    }
�ġ����Լ���benchmark���̴��jar��,��demo.benchmark.jar
�塢��demo.benchmark.jar�ŵ�dubbo.benchmark/libĿ¼��
��������duubo.properties
�ߡ�����run.bat(windows)��run.sh(linux)