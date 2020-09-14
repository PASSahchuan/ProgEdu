import { StudentEventsService } from './../../../services/student-events-log.service';
import { Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { StudashboardService } from './studashboard.service';
import { JwtService } from '../../../services/jwt.service';
import { User } from '../../../models/user';
import { TimeService } from '../../../services/time.service';

@Component({
  selector: 'app-studashboard',
  templateUrl: './studashboard.component.html'
})
export class StudashboardComponent implements OnInit {
  public assignmentTable: Array<any> = new Array<any>();
  public studentCommitRecord: JSON;
  public username: string;
  constructor(private studashboardService: StudashboardService, private timeService: TimeService,
    private jwtService?: JwtService, private router?: Router, private studentEventsService?: StudentEventsService) {
  }

  async ngOnInit() {
    this.username = new User(this.jwtService).getUsername();
    // assignment dasgboard viewed event emit
    const exit_event = {
      event: 'progedu.dashboard.assignment.viewed', event_type: 'view', context: 'progedu.dashboard.assignment.viewed',
      username: this.username, page: this.router.url, time: new Date().toISOString()
    };
    this.studentEventsService.createReviewRecord(exit_event).subscribe();
    await this.getAllAssignments();
    await this.getStudentCommitRecords();
  }

  async getAllAssignments() {
    this.studashboardService.getAllAssignments().subscribe(response => {
      this.assignmentTable = response.allAutoAssessment;
    });
  }

  async getStudentCommitRecords() {
    // clear student array
    this.studashboardService.getStudentCommitRecord(this.username).subscribe(response => {
      this.studentCommitRecord = response;
      console.log(this.studentCommitRecord);
    });
  }

  isRelease(release: Date) {
    const now_time = new Date().getTime();
    const realease_time = new Date(this.timeService.getUTCTime(release)).getTime();
    if (now_time >= realease_time) {
      return true;
    }
    return false;
  }

  isNA(commit: any) {
    if (JSON.stringify(commit.commitRecord) !== '{}') {
      return false;
    }
    return true;
  }

}
