import { Category, Assessment } from './../review-metrics-management/Category';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FormArray, FormGroup } from '@angular/forms';
import { environment } from '../../../../environments/environment';
import {AddJwtTokenHttpClient} from '../../../services/add-jwt-token.service';
import { JwtService } from '../../../services/jwt.service';
import { AssignmentAPI } from '../../../api/AssignmentAPI';
import { CategoryMetricsAPI } from '../../../api/CategoryMetricsAPI';


const createAssigmentOptions = ({
  headers: new HttpHeaders(
  )
});
@Injectable({
  providedIn: 'root'
})
export class CreateAssignmentService {
  CREATE_ASSIGNMENT_API = AssignmentAPI.createAssignment;
  CREATE_ASSIGNMENT_WITH_ORDER_API = AssignmentAPI.createAutoAssessment;
  MODIFY_ORDER_API = AssignmentAPI.modifyAssignmentOrderFile;
  GET_ALL_CATEGORY_API = CategoryMetricsAPI.getCategory;
  GET_METRICS_API = CategoryMetricsAPI.getMetrics;
  CREATE_REVIEW_ASSIGNMENT_API = AssignmentAPI.createPeerReviewAssignment;

  GET_ASSIGNMENT_FILE_API = AssignmentAPI.getAssignmentFile;




  constructor(private addJwtTokenHttpClient: AddJwtTokenHttpClient) { }

  createAssignment(assignment: FormGroup): Observable<any> {

    const formData = new FormData();

    formData.append('assignmentName', assignment.value.name);
    formData.append('releaseTime', new Date(assignment.value.releaseTime).toUTCString());
    formData.append('deadline', new Date(assignment.value.deadline).toUTCString());
    formData.append('readMe', assignment.value.description);
    formData.append('fileRadio', assignment.value.type);
    formData.append('file', assignment.value.file);

    return this.addJwtTokenHttpClient.post(this.CREATE_ASSIGNMENT_API, formData, createAssigmentOptions);
  }

  createAssignmentWithOrder(assignment: FormGroup): Observable<any> {

    const formData = new FormData();

    formData.append('assignmentName', assignment.value.name);
    formData.append('releaseTime', new Date(assignment.value.releaseTime).toUTCString());
    formData.append('deadline', new Date(assignment.value.deadline).toUTCString());
    formData.append('readMe', assignment.value.description);
    formData.append('fileRadio', assignment.value.type);
    formData.append('file', assignment.value.file);
    formData.append('order',assignment.value.assOrder);

    return this.addJwtTokenHttpClient.post(this.CREATE_ASSIGNMENT_WITH_ORDER_API, formData, createAssigmentOptions);
  }

  modifyOrder(assignment: FormGroup): Observable<any> {

    const formData = new FormData();
    
    formData.append('assignmentName', assignment.value.name);
    formData.append('fileRadio', assignment.value.type);
    formData.append('order',assignment.value.assOrder);
    
    return this.addJwtTokenHttpClient.post(this.MODIFY_ORDER_API, formData, createAssigmentOptions);
  }

  getAllCategory(): Observable<any> {
    return this.addJwtTokenHttpClient.get(this.GET_ALL_CATEGORY_API);
  }
  getMetrics(category: Category): Observable<any> {
    const params = new HttpParams().
    set('category', category.id.toString());
    return this.addJwtTokenHttpClient.get(this.GET_METRICS_API , { params });
  }

  createPeerReviewAssignment(assignment: FormGroup, metrics: number[]): Observable<any> {
    const formData = new FormData();
    
    (<FormArray>assignment.get('reviewTime')).controls.forEach(element => {
      element.get('startTime').setValue(new Date(element.get('startTime').value).toUTCString());
      element.get('endTime').setValue(new Date(element.get('endTime').value).toUTCString());
      element.get('reviewStartTime').setValue(new Date(element.get('reviewStartTime').value).toUTCString());
      element.get('reviewEndTime').setValue(new Date(element.get('reviewEndTime').value).toUTCString());
    })
    
    formData.append('assignmentName', assignment.value.name);
    formData.append('readMe', assignment.value.description);
    formData.append('fileRadio', assignment.value.type);
    formData.append('file', assignment.value.file);
    formData.append('amount', assignment.value.commitRecordCount);
    formData.append('reviewTime', JSON.stringify(assignment.value.reviewTime));
    formData.append('metrics', metrics.toString());

    return this.addJwtTokenHttpClient.post( this.CREATE_REVIEW_ASSIGNMENT_API, formData, createAssigmentOptions);
  }

  getAssignmentFile(assigememtName: string): string {
    const jwtService = new JwtService();
    return this.GET_ASSIGNMENT_FILE_API + '?fileName=' + assigememtName 
        + "&token=" + jwtService.getToken();
  }
}
