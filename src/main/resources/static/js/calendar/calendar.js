    ////////////////////////     모달 설정
      const myModal = $("#myModal");
      const startOpt = $('#start option');
      const endOpt = $('#end option');
    	  
      myModal.on("hidden.bs.modal", function () {
        $("#form-modal")[0].reset();
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

      ////////////////////////		캘린더 기능
      calendar.on("eventAdd", ()=>{
        myModal.modal('hide');
      });

      calendar.on("eventClick", (info)=>{
    	$(".modal-title").text("일정 정보");
    	$("#btn-insert").hide();
    	$("#btn-modify").show();
    	$("#btn-delete").show();
    	console.log(info.event.id);
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
			$("#sch-title, #sch-content").attr("disabled", true);
			
	 	  	myModal.modal('show');
		})
		.fail(function(jqXHR){
			alert("실패");
			alert(jqXHR.statusText + '(' + jqXHR.status + ')');  					
		})    
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
      let schedule;
      const selectScheType = $("input:radio[name=notice-or-vac]:checked").val();
      const selectVacType = $("input:radio[name=var-elem-radio]:checked").val();
      
      	if(selectScheType == 'notice'){
	         schedule = {
				  id: (calendar.getEvents()).length,
	              title: $("#sch-title").val(),
	              allDay: true, 
	              start: startDt + "T" + $("#start option:selected").val(),
	              end : endDt + "T" + $("#end option:selected").val(),
	              backgroundColor : "red"
	          };			
		}
		else if(selectScheType == 'mysche') {
	         schedule = {
				  id: (calendar.getEvents()).length,
	              title: $("#sch-title").val(),
	              allDay: true, 
	              start: startDt + "T" + $("#start option:selected").val(),
	              end : endDt + "T" + $("#end option:selected").val(),
	              backgroundColor : "blue"
	          };			
		} 
		else if(selectScheType == 'vac') {
	        if(selectVacType == 'all'){ // 전체일정
	         schedule = {
				  id: (calendar.getEvents()).length,
	              title: $("#sch-title").val(),
	              allDay: true, 
	              start: startDt,
	              end : endDt,
	              backgroundColor : "green"
	          };
	        }
	        else if(selectVacType != 'all'){	// 지정시간일정
	          schedule = {
				  id: (calendar.getEvents()).length,
	              title: $("#sch-title").val(),
	              allDay: false, 
	              start:  startDt + "T" + $("#start option:selected").val(),
	              end : startDt + "T" + $("#end option:selected").val(),
	              backgroundColor : "green"
	            };
	          }
		}
     	console.log(schedule.id);
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
      function fnAddScheduleAndAnnual(idNum){
	 	const currentDate = moment().format('YYYY-MM-DD');
	 	
		let annCt, schDiv;
		if($("input:radio[name=var-elem-radio]:checked").val() == 'all')
			annCt = 0;
		else if($("input:radio[name=var-elem-radio]:checked").val() == 'half')
			annCt = 1;
		else if($("input:radio[name=var-elem-radio]:checked").val() == 'quat')
			annCt = 2;
			
		if($("input:radio[name=notice-or-vac]:checked").val() == 'vac'){
	    const data = JSON.stringify({
		        'annId': idNum,
		        'annCtNo': annCt,
		        'annTitle': $("#sch-title").val(),
		        'annContent': $("#sch-content").val(),
		        'annTotal': 15,
		        'annStartTime':  startOpt.val(),
		        'annEndTime':  endOpt.val(),
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
		else if($("input:radio[name=notice-or-vac]:checked").val() == 'notice'
		|| $("input:radio[name=notice-or-vac]:checked").val() == 'mysche'){
			
		if($("input:radio[name=notice-or-vac]:checked").val() == 'notice')
			schDiv = 0;
		else if($("input:radio[name=notice-or-vac]:checked").val() == 'mysche')
			schDiv = 1;
			
	    const data = JSON.stringify({
		        'scheId': idNum,
		        'scheDiv': schDiv,
		        'scheTitle': $("#sch-title").val(),
		        'scheContent': $("#sch-content").val(),
		        'scheStartTime': startOpt.val(),
		        'scheEndTime': endOpt.val(),
		        'createDt': currentDate,
		        'creator': 1,
		        'modifyDt': currentDate, 
		        'modifier': 1,
		        'scheStat': annCt,
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