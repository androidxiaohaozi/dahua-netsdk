new Vue({
    el: '#app',
    data: {
        date: '',
        tableData: [],
        pageNum: 1,
        pageSize: 10,
        total: 0,
        Mask: false,
        Mask1: false,
        form: {},
        rules: {
            inspectionTime: [
                {required: true, message: '请选择时间', trigger: 'blur'}
            ],
            company: [
                {required: true, message: '请输入单位', trigger: 'blur'}
            ],
            personnel: [
                {required: true, message: '请输入人员', trigger: 'blur'}
            ],
            post: [
                {required: true, message: '请输入职务', trigger: 'blur'}
            ],
            keyInspectionPoints: [
                {required: true, message: '请输入重点检查单位', trigger: 'blur'}
            ],
            contentsProblems: [
                {required: true, message: '请输入内容及问题', trigger: 'blur'}
            ],
            // inspectionRemark: [
            //     { required: true, message: '请输入备注', trigger: 'blur' }
            // ]
        },
        fileList: [],
        imageUrl: '',
        dialogVisible: false,
        fileData: {
            id: ''
        },
        ffileList: [],
        dialogImageUrl: '',
        uploadFileUrl: HOST + '/minio/upload',
        Mask6: false,
    },
    created: function () {
    },
    watch: {},
    methods: {
        tableDateList(num) {
            // debugger
            this.pageNum = num ? this.pageNum : 1
            let id = getUrlParam("wxUserId");
            let ajaxUrl = PATH.select + `?pageSize=${this.pageSize}&pageNum=${this.pageNum}`;
            GetAjax(ajaxUrl, 'get', false, (data) => {
                if (data.code == 200) {
                    debugger
                    this.tableData = data.data
                } else {
                }
            });
        },
        handleCurrentChange(val) {
            this.pageNum = val;
            this.tableDateList(val);
        },
        insert() {
            this.Mask = true
            this.form = {
                inspectionTime: '',
                company: '',
                personnel: '',
                post: '',
                keyInspectionPoints: '',
                contentsProblems: '',
                inspectionRemark: '',
                imageList: [],
            }
            this.fileList = [];
            this.ffileList = []
        },
        addOpen() {
            let that = this
            let params = {
                serialNumber: this.form.serialNumber,
                cardNo: this.form.cardNo,
                userId: this.form.userId,
                cardName: this.form.cardName,
                cardPasswd: this.form.cardPasswd,
                cardStatus: this.form.cardStatus,
                cardType: this.form.cardType,
                cardTypeName: this.form.cardTypeName,
                useTimes: this.form.useTimes,
                firstEnter: this.form.firstEnter,
                enable: this.form.enable,
                startTime: this.form.startTime,
                recordNo: this.form.recordNo,
                endTime: this.form.endTime,
                imageList: this.form.imageList,
                typeOfWork:this.form.typeOfWork,
                subpackage:this.form.subpackage
            }
            Hajax1(PATH.insert, 'post', false, params, function (data) {
                // this.$http.post("http://192.168.8.94:8888/wechat/inspectDaily/insert",params,{emulateJSON: false}).then((res) => {
                if (data.code == 0) {
                    // alert("提交成功")
                    //location.reload()
                    that.Mask = false
                    //清空输入框
                    that.form = {}
                    that.tableDateList(that.pageNum)
                } else {
                    // this.$message.error(`添加失败`)
                }
            });
        },
        //新增取消按钮
        uploadClosed() {
            this.Mask = false;
            this.Mask1 = false;
            this.Mask6 = false
            this.form = {}
            this.fileList = [];
        },
        beforeRemove(file, fileList) {
            return this.$confirm(`确定移除 ${file.name}？`);
        },
        handleRemove(file, fileList) {
            const findex = this.form.imageList.map(f => f.url).indexOf(file.imageUrl);
            console.log(findex)
            if (findex > -1) {
                this.form.imageList.splice(findex, 1);
            }
        },
        handlePictureCardPreview(file) {//这里需要注意 用的img并不是file，因为接口文档给的是img，我们在upload中定义个name=“img”参数就可以了
            // // 检查文件类型
            // const isImage = file.raw.type.includes("image");
            // if (!isImage) {
            //     this.$message.error("上传文件类型必须是图片!");
            //     return false
            // }
            // 检查文件大小
            if (file.size > (2 * 1024 * 1024)) {
                this.$message.error(`上传文件大小不能超过10Mb`);
                this.$refs['refUpload'].handleRemove(file);
                return false;
            }
            // 检查文件数量
            if (this.fileList.length > 10) {
                this.$message.error(`上传文件最大数量为1`);
                this.$refs['refUpload'].handleRemove(file);
                return false;
            }
            this.dialogImageUrl = file.url;
            this.dialogVisible = true;
        },
        handleimg(res, file, fileList) {
            if (res.code === 200) {
                debugger;
                var fileName = res.data.fileName
                this.form.imageList.push({
                    imageUrl: fileName
                })
                this.$message.success(`图片${file.name}上传成功`)
            } else {
                this.$message.error(`图片${file.name}上传失败`)
            }
        },
        //删除
        BatchDelete(row) {
            let ajaxUrl = PATH.delete + `?id=${row.id}`;
            GetAjax(ajaxUrl, 'get', false, (data) => {
                if (data.code == 0) {
                    //alert("删除成功")
                    this.tableDateList();
                } else {

                }
            });
        },
        //修改按钮
        xiugai(row) {
            this.ffileList = []
            this.form = jQuery.extend(true, {}, row);
            this.form.imageList = this.form.imageList ? this.form.imageList : []
            this.Mask1 = true
            if (row.imageList != null) {
                for (let i = 0; i < row.imageList.length; i++) {
                    this.ffileList.push({
                        'name': row.imageList[i].imageUrl,
                        'url': row.imageList[i].imageAllUrl
                    })
                }
                // console.log(222,this.ffileList)
            } else {
            }
        },
        changeCrrunt() {
            let that = this
            let params = {
                id: this.form.id,
                inspectionTime: this.form.inspectionTime,
                company: this.form.company,
                personnel: this.form.personnel,
                post: this.form.post,
                keyInspectionPoints: this.form.keyInspectionPoints,
                contentsProblems: this.form.contentsProblems,
                inspectionRemark: this.form.inspectionRemark,
                imageList: this.form.imageList
            }
            Hajax1(PATH.update, 'post', false, params, function (data) {
                //this.$http.post("http://192.168.8.94:8888/wechat/inspectDaily/update",params,{emulateJSON: false}).then((res) => {
                if (data.code == 0) {
                    //alert("提交成功")
                    // location.reload()
                    that.Mask1 = false
                    that.form = {}
                    that.tableDateList(that.pageNum)
                } else {
                    //  this.$message.error(`修改失败`)
                }
            });
        },
        lookAllInfo() {
            this.Mask6 = true
        },
        cardStatusTextFormat(row,column,cellValue,index){
            const val = row["cardStatus"];
            if (val === -1) {
                return "未知";
            } else if(val === 0) {
                return "正常";
            } else if (val === 1) {
                return "挂失";
            } else if (val === 2) {
                return "注销";
            } else if (val === 3) {
                return "冻结";
            } else if (val === 4) {
                return "欠费";
            } else if (val === 5) {
                return "逾期";
            } else if (val === 6) {
                return "预欠费";
            } else {
                return '--'
            }
        },
        cardTypeTextFormat(row,column,cellValue,index){
            const val = row["cardType"];
            if (val === -1) {
                return "未知";
            } else if(val === 0) {
                return "一般卡";
            } else if (val === 1) {
                return "VIP卡";
            } else if (val === 2) {
                return "来宾卡";
            } else if (val === 3) {
                return "巡逻卡";
            } else if (val === 4) {
                return "黑名单卡";
            } else if (val === 5) {
                return "胁迫卡";
            } else if (val === 6) {
                return "巡检卡";
            } else if (val === 7) {
                return "母卡";
            } else {
                return '--'
            }
        },
        firstEnterTextFormat(row,column,cellValue,index){
            const val = row["firstEnter"];
            if (val === 0) {
                return "不是";
            } else if(val === 1) {
                return "是";
            } else {
                return '--'
            }
        },
        enableTextFormat(row,column,cellValue,index){
            const val = row["enable"];
            if (val === 0) {
                return "不是";
            } else if(val === 1) {
                return "是";
            } else {
                return '--'
            }
        }
    },
    mounted: function () {
        this.tableDateList()
    },
    updated: function () {
    },

});
