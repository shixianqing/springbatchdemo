#springbatch
## job的创建使用
 
  - job：作业，是批处理中的核心概念，是batch操作的基础单元，每个job由多个step组成
  - step：步骤，任务完成的节点
  - 每个job是由JobBuildFactory创建，每个step是由StepBuildFactory创建
  - 示例：
  
  
    ``
        @Configuration
        @EnableBatchProcessing //开启批处理
        public class JobConfiguration {
        
            /**
             * 创建任务对象的对象
             */
            @Autowired
            private JobBuilderFactory jobBuilderFactory;
        
            /**
             * 创建步骤对象的对象
             */
            @Autowired
            private StepBuilderFactory stepBuilderFactory;
        
            @Bean
            public Job helloJob(){
        
                return jobBuilderFactory.get("helloJob").start(helloStep1()).build();
            }
        
            @Bean
            public Step helloStep1(){
        
                return stepBuilderFactory.get("helloStep1").tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("hello world");
                        return RepeatStatus.FINISHED;
                    }
                }).build();
            }
        }  
    ``

