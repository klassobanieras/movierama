import React, { Component } from 'react';
import { createMovie } from '../utils/APIClient';
import { MOVIE_TITLE_MAX_LENGTH, MOVIE_DESCRIPTION_MAX_LENGTH } from '../constants/Constants';
import './newMovie.css';  
import { Form, Input, Button, notification } from 'antd';
const FormItem = Form.Item;
const { TextArea } = Input;

class NewMovie extends Component {
    constructor(props) {
        super(props);
        this.state = {
            description: {
                text: ''
            },
            title: {
                text: ''
            },
        };
        
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleDescriptionChange = this.handleDescriptionChange.bind(this);
        this.handleTitleChange = this.handleTitleChange.bind(this);
        this.isFormInvalid = this.isFormInvalid.bind(this);
    }

    handleSubmit() {
        const movieData = {
            description: this.state.description.text,
            title: this.state.title.text,
        };

        createMovie(movieData)
        .then(response => {
            notification.success({
                message: 'Movierama',
                description: "You successfully add a new movie.",
            });
            this.props.history.push("/");
        }).catch(error => {
            if(error.status === 401) {
                this.props.handleLogout('/login', 'error', 'You have been logged out. Please login create movie.');    
            } else {
                notification.error({
                    message: 'Movierama',
                    description: error.message || 'Sorry! Something went wrong. Please try again!'
                });              
            }
        });
    }

    validateDescription = (descriptionText) => {
        if(descriptionText.length === 0) {
            return {
                validateStatus: 'error',
                errorMsg: 'Please enter a description'
            }
        } else if (descriptionText.length > MOVIE_DESCRIPTION_MAX_LENGTH) {
            return {
                validateStatus: 'error',
                errorMsg: `Description is too long. Only ${MOVIE_DESCRIPTION_MAX_LENGTH} characters allowed!`
            }    
        } else {
            return {
                validateStatus: 'success',
                errorMsg: null
            }
        }
    }

    validateTitle = (titleText) => {
        if(titleText.length === 0) {
            return {
                validateStatus: 'error',
                errorMsg: 'Please enter a title'
            }
        } else if (titleText.length > MOVIE_DESCRIPTION_MAX_LENGTH) {
            return {
                validateStatus: 'error',
                errorMsg: `Title is too long. Only ${MOVIE_TITLE_MAX_LENGTH} characters allowed!`
            }    
        } else {
            return {
                validateStatus: 'success',
                errorMsg: null
            }
        }
    }

    handleDescriptionChange(event) {
        const value = event.target.value;
        this.setState({
            description: {
                text: value,
                ...this.validateDescription(value)
            }
        });
    }

    handleTitleChange(event) {
        const value = event.target.value;
        this.setState({
            title: {
                text: value,
                ...this.validateTitle(value)
            }
        });
    }

    isFormInvalid() {
        if(this.state.description.validateStatus !== 'success') {
            return true;
        }

        if(this.state.title.validateStatus !== 'success') {
            return true;
        }
    }

    render() {
        return (
            <div className="new-movie-container">
                <h1 className="page-title">Add a new Movie</h1>
                <div className="new-movie-content">
                    <Form onFinish={this.handleSubmit} onSubmit={this.handleSubmit} className="create-movie-form">
                        <FormItem validateStatus={this.state.title.validateStatus}
                            help={this.state.title.errorMsg} className="movie-form-row">
                            <TextArea 
                                placeholder="Enter your title"
                                style = {{ fontSize: '16px' }} 
                                autosize={{ minRows: 1, maxRows: 2 }} 
                                name = "title"
                                value = {this.state.title.text}
                                onChange = {this.handleTitleChange} />
                        </FormItem>

                        <FormItem validateStatus={this.state.description.validateStatus}
                            help={this.state.description.errorMsg} className="movie-form-row">
                            <TextArea 
                                placeholder="Enter your description"
                                style = {{ fontSize: '16px' }} 
                                autosize={{ minRows: 4, maxRows: 8 }} 
                                name = "description"
                                value = {this.state.description.text}
                                onChange = {this.handleDescriptionChange} />
                        </FormItem>

                        <FormItem className="movie-form-row">
                            <Button type="primary" 
                                htmlType="submit" 
                                size="large" 
                                disabled={this.isFormInvalid()}
                                className="create-movie-form-button">Create Movie</Button>
                        </FormItem>
                    </Form>
                </div>    
            </div>
        );
    }
}


export default NewMovie;