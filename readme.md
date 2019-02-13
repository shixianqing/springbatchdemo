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
    . JdbcPagingItemReader
    
           . 设置数据源
           . 设置查询条件
           . 将查询结果映射成实体类
    . 代码示例：
        
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
    