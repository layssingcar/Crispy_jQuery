      ////////////////////////     캘린더 설정
      let startDt, endDt;
      let loadEventList = [];
      const calendarEl = document.getElementById('calendar');
      const calendarHeader = {
        left: 'prev,next today',          
        center: 'title',
        right:'dayGridMonth,timeGridWeek,listWeek'        
      }

      const calendarOpt = {
        headerToolbar: calendarHeader,
        initialView: 'dayGridMonth',
        selectable:true,    // 달력 날짜 드래그
        locale:'kr',      // 달력 언어 설정
        dayMaxEventRows:true
      }

      const calendar = new FullCalendar.Calendar(calendarEl, calendarOpt);
      calendar.render();
	  fnLoadCalendarData();
      
	 ////////////////////////		캘린더 기능
     function fnLoadCalendarData(){
		$.ajax({
			type:'GET',
			url:'/crispy/getAnnList',
			data: 'empNo=' + empNo,
			contentType: 'application/json',
			dataType:'json'
		})
		.done(function(annList){
			$.ajax({
				type:'GET',
				url:'/crispy/getScheList',
				data: 'empNo=' + empNo,
				contentType: 'application/json',
				dataType:'json'
			})
			.done(function(scheList){
				$(scheList).each(function(){
					loadEventList.push({
						id: this.scheId,
						title:this.scheTitle,
						start:this.scheStartTime,
						end:this.scheEndTime,
		              	allDay: true, 
		              	backgroundColor : (this.scheDiv == 0) ? "rgba(255, 0, 0, 0.7)" : "rgba(0, 0, 255, 0.7)",
		              	borderColor: (this.scheDiv == 0) ? "rgba(255, 0, 0, 0.7)" : "rgba(0, 0, 255, 0.7)"			
					});
				});
				$(annList).each(function(){
					loadEventList.push({
						id: this.annId,
						title:this.annTitle,
						start:this.annStartTime,
						end:this.annEndTime,
		              	allDay: (this.annCtNo == 0) ? true : false, 
      		  			backgroundColor : "rgba(0, 135, 0, 0.7)",
              			borderColor: "rgba(0, 135, 0, 0.7)"								
					});
				});		
				for(var i = 0; i < loadEventList.length; i++)
						calendar.addEvent(loadEventList[i]);
						
				calendar.refetchEvents();
			})
			.fail(function(){
				alert("연차 불러오기 실패");	
			})
		})
		.fail(function(){
			alert("일정 불러오기 실패");
		})
	 }
      
      // 시작시간 종료시간
    	function fnSetSelectByValue(selectId, value){
			for(var i = 0; i < selectId.length; i++){
				if(selectId.eq(i).val() == value){
					selectId.eq(i).prop('selected', true);
					break;
				}
			}
		}
		
		function fnSetModalDetailToggle(state){
			$("#sch-title, #sch-content").prop("disabled", state);
			$("#start, #end").prop("disabled", state);
		  	$("input[name='notice-or-vac']").prop("disabled", state);
		  	$("input[name='var-elem-radio']").prop("disabled", state);			
		  	$("input[name='btnradio']").prop("disabled", state);			
		}
		
		function fnShowSelectEvent(info){
	 	if(info.event.id.substring(0, 2) == "가맹" || info.event.id.substring(0, 2) == "개인"){
		    	$.ajax({
					type:'GET',
					url: '/crispy/getScheById',
			        contentType: 'application/json',
					data:'scheId=' + info.event.id,
					dataType:'json'
			    })
				.done(function(data){
					$("#sch-title").val(data.scheTitle);
					$("#sch-content").val(data.scheContent);
				  	fnSetSelectByValue(startOpt, data.scheStartTime.substring(11, 17));
				  	fnSetSelectByValue(endOpt, data.scheEndTime.substring(11, 17));
					fnSetModalDetailToggle(1);
					radioBtnsByNotiorVac[data.scheDiv].checked = true;
			 	  	myModal.modal('show');
				})
				.fail(function(jqXHR){
					alert("실패");
					alert(jqXHR.statusText + '(' + jqXHR.status + ')');  					
				})    
			}
			else if(info.event.id.substring(0, 2) == "연차"){
		    	$.ajax({
					type:'GET',
					url: '/crispy/getAnnById',
			        contentType: 'application/json',
					data:'annId=' + info.event.id,
					dataType:'json'
			    })
				.done(function(data){
					$("#sch-title").val(data.annTitle);
					$("#sch-content").val(data.annContent);
				  	fnSetSelectByValue(startOpt, data.annStartTime.substring(11, 17));
				  	fnSetSelectByValue(endOpt, data.annEndTime.substring(11, 17));
					fnSetModalDetailToggle(1);
					radioBtnsByNotiorVac[2].checked = true;
					radioBtnsByVacType[data.annCtNo].checked = true;
			 	  	
			 	  	if(data.annCtNo != 0)
						$("#vac-type, #vac-elem").show();
					else
						$("#vac-type, #vac-elem").hide();
						
			 	  	myModal.modal('show');
				})
				.fail(function(jqXHR){
					alert("실패");
					alert(jqXHR.statusText + '(' + jqXHR.status + ')');  					
				})    			
			}			
		}
		
      calendar.on("eventAdd", ()=>{
        myModal.modal('hide');
      });

      calendar.on("eventClick", (info)=>{
    	$(".modal-title").text("일정 정보");
    	$("#btn-insert, #btn-update, #btn-cancle").hide();
    	$("#btn-modify, #btn-delete").show();
  		fnShowSelectEvent(info);
  		selectScheduleId = info.event.id;
  		startDt = info.event.startStr;
  		endDt = info.event.endStr;
      });

      calendar.on("select", (info)=>{
   	 	$(".modal-title").text("일정 등록");  
    	$("#btn-insert").show();
    	$("#btn-modify, #btn-delete, #btn-update, #btn-cancle").hide();
  	  	myModal.modal('show');
        startDt = info.startStr;
        endDt = info.endStr;
      });

      
	  