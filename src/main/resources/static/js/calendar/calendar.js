    ////////////////////////     모달 설정
      const myModal = $("#myModal");
      const startOpt = $('#start option');
      const endOpt = $('#end option');
      const radioBtnsByNotiorVac = document.getElementsByName('notice-or-vac');
      const radioBtnsByVacType = document.getElementsByName('var-elem-radio');
    	  
      myModal.on("hidden.bs.modal", function () {
        $("#form-modal")[0].reset();
        fnSetModalDetailToggle(0);
	 	startOpt.eq(0).prop('selected', true);	// 시작시간 초기화
	 	endOpt.eq(0).prop('selected', true);		// 종료시간 초기화
      });
      
      myModal.on("show.bs.modal", function(){
		    // 초기 체크 상태에 따라 show/hide 설정
		    if ($("#radio-notice").is(':checked')) {
	            $("#vac-type, #vac-elem").hide();
            	$(".form-group-start, .form-group-end").show();
		    }
		    
		    $("input[name='notice-or-vac']").off('click').on('click', function() {		// 공지, 휴가 라디오 버튼 눌렀을 때 
		        if ($("#radio-notice").is(':checked') || $("#radio-mysche").is(':checked')) {		// 공지
		            $("#vac-type, #vac-elem").hide();
	            	$(".form-group-start, .form-group-end").show();
            	 	$("#start").off();									// 일정구분 라디오 초기화	
            	 	startOpt.eq(0).prop('selected', true);	// 시작시간 초기화
            	 	endOpt.eq(0).prop('selected', true);		// 종료시간 초기화
            	 	$("#end").attr("disabled", false);
		            $("#vac-all").prop("checked", true);
            	 	
	        	} else if ($("#radio-vac").is(':checked')) {		// 휴가
		            $("#vac-type, #vac-elem").show();
		            
		            if($("#vac-all").is(':checked')){	// 연차
		            	$(".form-group-start, .form-group-end").hide();
		            	$("#end").attr("disabled", false);
		            }
		            $("input[name='var-elem-radio']").off('click').on('click', function(){		// 휴가 종류 라디오 버튼 눌렀을 때 
		            	 if ($("#vac-all").is(':checked')) {	// 연차
		            		 $(".form-group-start, .form-group-end").hide();
		 		        } else if ($("#vac-half").is(':checked')){	// 반차
		 		        	$(".form-group-start, .form-group-end").show();
			            	$("#end").attr("disabled", true);
			            	radioControl(7);
			            	
		 		        } else if($("#vac-quat").is(':checked')){	// 반반차
		 		        	$(".form-group-start, .form-group-end").show();
			            	$("#end").attr("disabled", true);
			            	radioControl(3);
		 		        }
		            })
		        }
		    });
		});

      const fntest = ()=>{    // 고른 시간 이후의 시간대만 표시되도록 하는 함수
        var index = $("#start option").index($("#start option:selected")); // 내가 고른 셀렉트의 인덱스
        var maxsize = $("#start option").length;
        for(var i = index + 1; i < maxsize; i++)
        {
          var newOption = $('<option>').attr('value', $("#start option:eq(" + i + ")").val()).text($("#start option:eq(" + i + ")").val());
          $("#end").append(newOption);
        }
      }
      
      function radioControl(num){
      	if($("#btnradio2").is(":checked"))
    		startOpt.eq(10).prop('selected', true);
    	else if($("#btnradio1").is(":checked"))
    		startOpt.eq(0).prop('selected', true);
    	
    	$("#btnradio2").off('click').on('click', function(){
    		startOpt.eq(10).prop('selected', true);
    		if((startOpt.index($("#start option:selected")) + num) >= startOpt.length)
    			endOpt.eq(startOpt.length - 1).prop('selected', true);
    		else
    			endOpt.eq(startOpt.index($("#start option:selected")) + num).prop('selected', true);
    	})
    	
    	$("#btnradio1").off('click').on('click', function(){
    		startOpt.eq(0).prop('selected', true);
    		if((startOpt.index($("#start option:selected")) + num) >= startOpt.length)
    			endOpt.eq(startOpt.length - 1).prop('selected', true);
    		else
    			endOpt.eq(startOpt.index($("#start option:selected")) + num).prop('selected', true);
    	})
    	
    	$("#start").off('change').on('change', function(){
    		if((startOpt.index($("#start option:selected")) + num) >= startOpt.length)
    			endOpt.eq(startOpt.length - 1).prop('selected', true);
    		else
    			endOpt.eq(startOpt.index($("#start option:selected")) + num).prop('selected', true);
    	})
    	
		if((startOpt.index($("#start option:selected")) + num) >= startOpt.length)
			endOpt.eq(startOpt.length - 1).prop('selected', true);
		else
			endOpt.eq(startOpt.index($("#start option:selected")) + num).prop('selected', true);    	  
      }

      ////////////////////////     캘린더 설정
      let startDt, endDt;
      let loadEventList = [];
      
     function fnLoadCalendarData(){
		$.ajax({
			type:'GET',
			url:'/crispy/getAnnList',
			contentType: 'application/json',
			dataType:'json'
		})
		.done(function(annList){
			$.ajax({
				type:'GET',
				url:'/crispy/getScheList',
				contentType: 'application/json',
				dataType:'json'
			})
			.done(function(scheList){
				console.log(scheList);
				$(scheList).each(function(){
					if(this.scheDiv == 0){
						loadEventList.push({
							id: this.scheId,
							title:this.scheTitle,
							start:this.scheStartTime,
							end:this.scheEndTime,
			              	allDay: true, 
			              	backgroundColor : "rgba(255, 0, 0, 0.7)",
			              	borderColor: "rgba(255, 0, 0, 0.7)"							
						});
					}
					else if(this.scheDiv == 1){
						loadEventList.push({
							id: this.scheId,
							title:this.scheTitle,
							start:this.scheStartTime,
							end:this.scheEndTime,
			              	allDay: true, 
			              	backgroundColor : "rgba(0, 0, 255, 0.7)",
			              	borderColor: "rgba(0, 0, 255, 0.7)"								
						});						
					}
				});
				
				$(annList).each(function(){
					if(this.annCtNo == 0){
						loadEventList.push({
							id: this.annId,
							title:this.annTitle,
							start:this.annStartTime,
							end:this.annEndTime,
			              	allDay: true, 
          		  			backgroundColor : "rgba(0, 135, 0, 0.7)",
	              			borderColor: "rgba(0, 135, 0, 0.7)"								
						});
					}
					else if(this.annCtNo == 1){
						loadEventList.push({
							id: this.annId,
							title:this.annTitle,
							start:this.annStartTime,
							end:this.annEndTime,
             			 	allDay: false, 
          		  			backgroundColor : "rgba(0, 135, 0, 0.7)",
	              			borderColor: "rgba(0, 135, 0, 0.7)"							
						});					
					}
					else if(this.annCtNo == 2){
						loadEventList.push({
							id: this.annId,
							title:this.annTitle,
							start:this.annStartTime,
							end:this.annEndTime,
			              	allDay: false, 
		          		  	backgroundColor : "rgba(0, 135, 0, 0.7)",
		              		borderColor: "rgba(0, 135, 0, 0.7)"						
						});							
					}					
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
        editable:true,
        dayMaxEventRows:true
      }

      const calendar = new FullCalendar.Calendar(calendarEl, calendarOpt);
      calendar.render();
      
	  fnLoadCalendarData();
      ////////////////////////		캘린더 기능
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
			startOpt.prop("disabled", state);
			endOpt.prop("disabled", state);
		  	$("input[name='notice-or-vac']").prop("disabled", state);
		  	$("input[name='var-elem-radio']").prop("disabled", state);			
		}
		
		function fnShowSelectEvent(info){
	 	if(info.event.id.substring(0, 2) == "일정"){
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
			 	  	myModal.modal('show');
			 	  	
			 	  	if(data.annCtNo != 0)
						$("#vac-type, #vac-elem").show();
					else
						$("#vac-type, #vac-elem").hide();
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
    	$("#btn-insert").hide();
    	$("#btn-modify").show();
    	$("#btn-delete").show();
  		fnShowSelectEvent(info);
      });
      
      calendar.on("dateClick", (info)=>{
        });

      calendar.on("select", (info)=>{
   	 	$(".modal-title").text("일정 등록");  
    	$("#btn-insert").show();
    	$("#btn-modify").hide();
    	$("#btn-delete").hide();
  	  	myModal.modal('show');
        startDt = info.startStr;
        endDt = info.endStr;
      });
      
      function fnRegistSchedule() {		// 일정 등록 처리 함수
      let selectScheType = $("input:radio[name=notice-or-vac]:checked").val();	// 공지,개인,연차
      let selectVacType = $("input:radio[name=var-elem-radio]:checked").val();	// 연차,반차,반반차,
      let schedule;
      
      	if(selectScheType == 'notice'){
	         schedule = {
				  id: "일정" + (calendar.getEvents()).length,
	              title: $("#sch-title").val(),
	              start: startDt + "T" + $("#start option:selected").val(),
	              end : endDt + "T" + $("#end option:selected").val(),
	              allDay: true, 
	              backgroundColor : "rgba(255, 0, 0, 0.7)",
	              borderColor: "rgba(255, 0, 0, 0.7)"
	          };			
		}
		
		else if(selectScheType == 'mysche') {
	         schedule = {
				  id: "일정" + (calendar.getEvents()).length,
	              title: $("#sch-title").val(),
	              start: startDt + "T" + $("#start option:selected").val(),
	              end : endDt + "T" + $("#end option:selected").val(),
	              allDay: true, 
  	              backgroundColor : "rgba(0, 0, 255, 0.7)",
	              borderColor: "rgba(0, 0, 255, 0.7)"
	          };			
		} 
		else if(selectScheType == 'vac') {
	        if(selectVacType == 'all'){ // 전체일정
	         schedule = {
				  id: "연차" + (calendar.getEvents()).length,
	              title: $("#sch-title").val(),
	              start: startDt,
	              end : endDt,
	              allDay: true, 
	              backgroundColor : "rgba(0, 135, 0, 0.7)",
	              borderColor: "rgba(0, 135, 0, 0.7)"
	          };
	        }
	        else if(selectVacType != 'all'){	// 지정시간일정
	          schedule = {
				  id: "연차" + (calendar.getEvents()).length,
	              title: $("#sch-title").val(),
	              start:  startDt + "T" + $("#start option:selected").val(),
	              end : startDt + "T" + $("#end option:selected").val(),
	              allDay: false, 
          		  backgroundColor : "rgba(0, 135, 0, 0.7)",
	              borderColor: "rgba(0, 135, 0, 0.7)"
	            };
	          }
		}
        calendar.addEvent(schedule);
      	fnAddScheduleAndAnnual(schedule.id);
        calendar.unselect();
      };

      // 체크박스 관련 함수
      function fnClickCheckAll(){
    	  if ($('#allscheChecked').is(':checked')){
      	    $('.form-check-input').prop('checked', true);
      	  } else{
      	    $('.form-check-input').prop('checked', false);
      	  }
      }
      
      function fnClickCheckSingle(){
    	  if($('#allscheChecked').is(':checked'))
    		  $('#allscheChecked').prop('checked', false);
      	  else if(!$('#allscheChecked').is(':checked'))
      	  	if($('#shopscheChecked').is(':checked') && $('#myscheChecked').is(':checked')){
				$('#allscheChecked').prop('checked', true);
			}    		  
      }
      
      // 모달 ajax
      function fnAnnAjax(idNum, endTime, ctNo, currentDate){
	    const data = JSON.stringify({
		        'annId': idNum,
		        'annCtNo': ctNo,
		        'annTitle': $("#sch-title").val(),
		        'annContent': $("#sch-content").val(),
		        'annTotal': 15,
		        'annStartTime':  startDt + "T" + $("#start option:selected").val(),
		        'annEndTime':  endTime + "T" + $("#end option:selected").val(),
		        'createDt': currentDate,
		        'creator': 1,
		        'modifyDt': currentDate, 
		        'modifier': 1,
		        'empNo': 1
		    });			
		    $.ajax({
		        type: 'POST',
		        url: '/crispy/registAnn',
		        contentType: 'application/json',
		        dataType: 'json',
		        data: data
		    })
			.done(function(data){
				alert("연차 저장 성공");
			})
			.fail(function(jqXHR){
				alert("연차 저장 실패");
				alert(jqXHR.statusText + '(' + jqXHR.status + ')');  					
			})    		
	  }
      
      function fnAddScheduleAndAnnual(idNum){
	  	const currentDate = moment().format('YYYY-MM-DD');
	  	let selectScheType = $("input:radio[name=notice-or-vac]:checked").val();	// 공지,개인,연차
	  	let selectVacType = $("input:radio[name=var-elem-radio]:checked").val();	// 연차,반차,반반차,
	 	
		let annCt, schDiv;
		if(selectVacType == 'all')
			annCt = 0;
		else
			annCt = (selectVacType == 'half') ? 1 : 2;
			
		if(selectScheType == 'vac'){
			if(selectVacType == 'all')
 				fnAnnAjax(idNum,endDt, annCt, currentDate);
			else
				fnAnnAjax(idNum,startDt, annCt, currentDate);
		}
		else if(selectScheType == 'notice' || selectScheType == 'mysche'){
			schDiv = (selectScheType == 'notice') ? 0 : 1;

		    const data = JSON.stringify({
			        'scheId': idNum,
			        'scheDiv': schDiv,
			        'scheTitle': $("#sch-title").val(),
			        'scheContent': $("#sch-content").val(),
			        'scheStartTime': startDt + "T" + $("#start option:selected").val(), 
			        'scheEndTime': endDt + "T" + $("#end option:selected").val(),
			        'createDt': currentDate,
			        'creator': 1,
			        'modifyDt': currentDate, 
			        'modifier': 1,
			        'scheStat': 0,
			        'empNo': 1
			    });
			    $.ajax({
			        type: 'POST',
			        url: '/crispy/registSche',
			        contentType: 'application/json',
			        dataType: 'json',
			        data: data
			    })
				.done(function(data){
					alert("일정 저장 성공");
				})
				.fail(function(jqXHR){
					alert("일정 저장 실패");
					alert(jqXHR.statusText + '(' + jqXHR.status + ')');  					
				})     			
		}
	  }