# springbatch

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


## 从数据库读取
   - JdbcPagingItemReader
   
           . 设置数据源
           . 设置查询条件
           . 将查询结果映射成实体类
   - MySqlPagingQueryProvider // mysql分页查询支持器
   
   - 代码示例：
        
        ```
            JdbcPagingItemReader<User> jdbcPagingItemReader = new JdbcPagingItemReader<>();
            jdbcPagingItemReader.setDataSource(dataSource);
            jdbcPagingItemReader.setFetchSize(3);//每次从数据库拉取条数
    
            //设置查询条件
            MySqlPagingQueryProvider pagingQueryProvider = new MySqlPagingQueryProvider();
            pagingQueryProvider.setSelectClause("id,name,password");
            pagingQueryProvider.setFromClause("user");
            
            Map<String, Order> orderMap = new HashMap<>();
            orderMap.put("password",Order.DESCENDING);//设置排序字段
            pagingQueryProvider.setSortKeys(orderMap);
            jdbcPagingItemReader.setQueryProvider(pagingQueryProvider);
    
            //设置行映射器
            jdbcPagingItemReader.setRowMapper(new RowMapper<User>() {
                @Override
                public User mapRow(ResultSet resultSet, int i) throws SQLException {
                    User user = new User();
                    user.setId(resultSet.getInt(1));
                    user.setName(resultSet.getString(2));
                    user.setPassword(resultSet.getString(3));
                    return user;
                }
            });
            return jdbcPagingItemReader;
        
        ```
        

## 从txt文件中获取数据

   - FlatFileItemReader
   - DelimitedLineTokenizer //行分词器
   - DefaultLineMapper //行映射器，将一行数据映射成对应的实体
   - defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
   - flatFileItemReader.setLineMapper(defaultLineMapper);
   - 代码示例：
   
    ```
        FlatFileItemReader<Hospital> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setEncoding("utf-8");
        flatFileItemReader.setLinesToSkip(1);//跳过第一行
        flatFileItemReader.setResource(new ClassPathResource("/data/hospital.txt"));

        //解析数据  按行分词
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setNames("id","org_name","org_type","addr","allow_no","cert_dept",
                "start_valid_date","end_invalid_date");

        //行映射器
        DefaultLineMapper<Hospital> defaultLineMapper = new DefaultLineMapper<>();
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
        defaultLineMapper.setFieldSetMapper(new FieldSetMapper<Hospital>() {
            @Override
            public Hospital mapFieldSet(FieldSet fieldSet) throws BindException {
                Hospital hospital = new Hospital();
                hospital.setId(fieldSet.readInt("id"));
                hospital.setAddr(fieldSet.readString("addr"));
                hospital.setAllowNo(fieldSet.readString("allow_no"));
                hospital.setCertDept(fieldSet.readString("cert_dept"));
                hospital.setEndValidDate(fieldSet.readString("end_invalid_date"));
                hospital.setOrgName(fieldSet.readString("org_name"));
                hospital.setOrgType(fieldSet.readString("org_type"));
                hospital.setStartValidDate(fieldSet.readString("start_valid_date"));
                return hospital;
            }
        });
        defaultLineMapper.afterPropertiesSet();
        flatFileItemReader.setLineMapper(defaultLineMapper);

        return flatFileItemReader;
    ``` 

## 从xml中读取数据

- StaxEventItemReader  xml读取器
- pom引入jar包
    
    ``
    
        <dependency>
            <groupId>com.thoughtworks.xstream</groupId>
            <artifactId>xstream</artifactId>
            <version>1.4.11.1</version>
        </dependency>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-oxm</artifactId>
            <version>5.0.8.RELEASE</version>
        </dependency>
    ``
- xstream实现xml转对象
- spring-oxm遵循xstream接口规范，实现xml转对象
- 代码示例：
    
    ``
    
            @Bean
            public ItemReader<User> xmlItemReader() {
        
                StaxEventItemReader<User> staxEventItemReader = new StaxEventItemReader<>();
                staxEventItemReader.setFragmentRootElementName("user");//设置根标签
                staxEventItemReader.setResource(new ClassPathResource("/data/user.xml"));//设置文件路径
        
                //将xml转成实体对象
                XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
                Map<String,Class> alias = new HashMap<>();
                alias.put("user",User.class);//key 为根标签，class是key标签下的所有标签映射到的对象
                xStreamMarshaller.setAliases(alias);
        
                staxEventItemReader.setUnmarshaller(xStreamMarshaller);
        
                return staxEventItemReader;
            }
    
    ``
    
## 多文件读取
   - MultiResourceItemReader
   - 多文件读取，其实就是一个一个文件读取
   - MultiResourceItemReader需要设置文件读取代理，待读取文件资源
   - 代码示例：
    
        ``
        
        
        @Value("classpath:/data/file*.txt")
        private Resource[] resources;
        
        @Bean
        public MultiResourceItemReader<Hospital> multiFileItemReader() {
    
            MultiResourceItemReader<Hospital> multiResourceItemReader = new MultiResourceItemReader<>();
            //多文件读取，其实也是一个一个文件进行读取，需要设置文件读取器
            multiResourceItemReader.setDelegate(flatFileItemReader());
            multiResourceItemReader.setResources(resources);
            return multiResourceItemReader;
        }
    
        @Bean
        public FlatFileItemReader<Hospital> flatFileItemReader(){
            FlatFileItemReader<Hospital> flatFileItemReader = new FlatFileItemReader<>();
            flatFileItemReader.setEncoding("utf-8");
    
            DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
            delimitedLineTokenizer.setDelimiter(",");
            delimitedLineTokenizer.setNames("id","org_name","org_type","addr","allow_no","cert_dept",
                    "start_valid_date","end_invalid_date");
    
            DefaultLineMapper<Hospital> defaultLineMapper = new DefaultLineMapper<>();
            defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
            defaultLineMapper.setFieldSetMapper(new FieldSetMapper<Hospital>() {
                @Override
                public Hospital mapFieldSet(FieldSet fieldSet) throws BindException {
                    Hospital hospital = new Hospital();
                    hospital.setStartValidDate(fieldSet.readString("start_valid_date"));
                    hospital.setOrgType(fieldSet.readString("org_type"));
                    hospital.setOrgName(fieldSet.readString("org_name"));
                    hospital.setEndValidDate(fieldSet.readString("end_invalid_date"));
                    hospital.setCertDept(fieldSet.readString("cert_dept"));
                    hospital.setAllowNo(fieldSet.readString("allow_no"));
                    hospital.setAddr(fieldSet.readString("addr"));
                    hospital.setId(fieldSet.readInt("id"));
                    return hospital;
                }
            });
    
            flatFileItemReader.setLineMapper(defaultLineMapper);
    
            return flatFileItemReader;
        }
        ``
        
## 从文件中读取，并写入到数据库
- JdbcBatchItemWriter
- 代码示例：
    
    ```
    
        @Bean
        public ItemWriter<Hospital> jdbcBatchItemWriter() {
            JdbcBatchItemWriter<Hospital> jdbcBatchItemWriter = new JdbcBatchItemWriter<>();
            jdbcBatchItemWriter.setDataSource(dataSource);
            jdbcBatchItemWriter.setSql(
                    "INSERT INTO `hospital` (`id`, `org_name`, `org_type`, `addr`, `allow_no`, `cert_dept`, `start_valid_date`, `end_invalid_date`) " +
                            "VALUES (:id, :orgName, :orgType, :addr, :allowNo, :certDept, :startValidDate, :endValidDate)"
            );
            //将对象属性值映射到sql参数中
            jdbcBatchItemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
            return jdbcBatchItemWriter;
        }
    
    
    ```
     
## 从数据库读取，写入到普通文件
- FlatItemFileWriter  
- 代码示例：
    
    ```
        @Bean
        public ItemWriter<? super Hospital> flatFileItemWriter() {
    
            FlatFileItemWriter<Hospital> flatFileItemWriter = new FlatFileItemWriter<>();
            flatFileItemWriter.setEncoding("utf-8");
            //设置存放数据的文件路径
            flatFileItemWriter.setResource(new FileSystemResource("f:/hospital_generate.txt"));
    
            flatFileItemWriter.setLineAggregator(new LineAggregator<Hospital>() {
                ObjectMapper objectMapper = new ObjectMapper();
                @Override
                public String aggregate(Hospital item) {
                    String result = null;
                    try {
                        result = objectMapper.writeValueAsString(item);//将对象转json字符串
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
    
                    return result;
                }
            });
            return flatFileItemWriter;
    
        }
    ```
    
    
## 从数据库读取并写入到xml文件
- StaxEventItemWriter
- XstreamMarshaller - xml标签元素与实体对象映射
- 代码示例

    ```
        @Bean
        public ItemWriter<? super Hospital> xmlFileItemWriter() {
            StaxEventItemWriter<Hospital> staxEventItemWriter = new StaxEventItemWriter<>();
            staxEventItemWriter.setEncoding("utf-8");
            staxEventItemWriter.setResource(new FileSystemResource("f:/hospital.xml"));
            staxEventItemWriter.setRootTagName("hospitals");
    
            //XML标签与实体对象映射
            Map<String,Class<Hospital>> alias = new HashMap<>();
            alias.put("hospital",Hospital.class);
    
            XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
            xStreamMarshaller.setAliases(alias);
    
            staxEventItemWriter.setMarshaller(xStreamMarshaller);
    
            return staxEventItemWriter;
    
        }
    ```
    


## 错误重试
-   在读取数据，处理数据，写数据时，可以通过容错，设置重试次数，重试处理
-   faultTolerant() 使得step具备容错能力
-   retry(Class<? extends Throwable> type) 出现何种异常时，进行重试
-   retryLimit(int retryLimit) 重试次数
-   代码示例

    - step
    ```
       @Bean
        public Step retryDemoStep() {
            return stepBuilderFactory.get("retryDemoStep")
                    .<String,String>chunk(10)
                    .reader(retryDemoReader())
                    .processor(retryDemoProcess)
                    .writer(retryDemoWriter)
                    .faultTolerant()//容错
                    .retry(CustomerRetryException.class)//出现CustomerRetryException异常时，进行重试
                    .retryLimit(5)//重试次数，超过次数，任务结束
                    .build();
        }
    ```

    - reader
    ```
        @Bean
        public ListItemReader<String> retryDemoReader() {
            List<String> list = new ArrayList<>();
    
            for (int i=1;i<60;i++){
                list.add(String.valueOf(i));
            }
    
            return new ListItemReader<>(list);
        }
    
    ```
    - writer
    
    ```
    @Component("retryDemoWriter")
    public class RetryDemoWriter implements ItemWriter<String> {
    
    
        @Override
        public void write(List<? extends String> items) throws Exception {
    
           for (String item:items){
               System.out.println("----------输出："+item+"----------------");
           }
        }
    }
    ```
    - process
    
    ```
    @Component("retryDemoProcess")
    public class RetryDemoProcess implements ItemProcessor<String,String> {
    
        private int attemptCount;
    
        @Override
        public String process(String item) throws Exception {
    
            System.out.println("processing item is "+item);
    
            if ("26".equals(item)){
                attemptCount++;
                if (attemptCount >= 3){
                    System.out.println("retried "+attemptCount+" times success");
                    return String.valueOf(Integer.valueOf(item) * -1);
                } else {
    
                    throw new CustomerRetryException("process failed，attempt："+attemptCount);
                }
            }
            return String.valueOf(Integer.valueOf(item) * -1);
        }
    }
    ```
    
## 错误跳过

- 在读取数据，处理数据，写数据时，出现异常，跳过该异常数据
- faultTolerant() 使得step具备容错能力
- skip(Class<? extends Throwable> type) 出现异常时，跳过
- skipLimit(int skipLimit) 跳过次数

    - step
    ```
          @Bean
           public Step skipDemoStep() {
       
              return stepBuilderFactory.get("skipDemoStep")
                       .<String,String>chunk(10)
                       .reader(skipDemoReader())
                       .processor(skipDemoProcess)
                       .writer(skipDemoWriter)
                       .faultTolerant()
                       .skip(CustomerRetryException.class)
                       .skipLimit(5)
                       .build();
           }
    ```
    
    - reader
    ```
        @Bean
        public ListItemReader<String> skipDemoReader() {
            List<String> list = new ArrayList<>();
    
            for (int i=1;i<60;i++){
                list.add(String.valueOf(i));
            }
    
            return new ListItemReader<>(list);
        }
    ```
    
    - writer
    
    ```
    @Component("skipDemoWriter")
    public class SkipDemoWriter implements ItemWriter<String> {
    
    
        @Override
        public void write(List<? extends String> items) throws Exception {
    
           for (String item:items){
               System.out.println("----------输出："+item+"----------------");
           }
        }
    }
    ```
    
   - process
   
   ```
   @Component("skipDemoProcess")
   public class SkipDemoProcess implements ItemProcessor<String,String> {
   
       private int attemptCount;
   
       @Override
       public String process(String item) throws Exception {
   
           System.out.println("processing item is "+item);
   
           if ("26".equals(item)){
               attemptCount++;
               if (attemptCount >= 3){
                   System.out.println("retried "+attemptCount+" times success");
                   return String.valueOf(Integer.valueOf(item) * -1);
               } else {
   
                   throw new CustomerRetryException("process failed，attempt："+attemptCount);
               }
           }
           return String.valueOf(Integer.valueOf(item) * -1);
       }
   }
   ```
   
## 错误跳过监听器

- 可以监听发生错误时，做出相关处理
- 需要实现SkipListener接口
- listener(SkipListener<? super I, ? super O> listener) 给step添加监听
- 代码示例
    
     - step
     ```
         @Bean
         public Step skipListenerDemoStep() {
     
             return stepBuilderFactory.get("skipListenerDemoStep")
                     .<String,String>chunk(10)
                     .reader(skipListenerDemoReader())
                     .processor(skipDemoProcess)
                     .writer(skipDemoWriter)
                     .faultTolerant()
                     .skip(CustomerRetryException.class)
                     .skipLimit(5)
                     .listener(mySkipListener)
                     .build();
         }
     ```