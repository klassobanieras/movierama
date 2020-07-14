import React, { Component } from 'react';
import './Movie.css';
import { Avatar } from 'antd';
import { Link } from 'react-router-dom';
import {LikeOutlined, DislikeOutlined, CalendarOutlined, PlusOutlined} from '@ant-design/icons';
import { getAvatarColor } from '../utils/Colors';
import { formatDateTime } from '../utils/Helpers';

import { Radio, Button } from 'antd';
const RadioGroup = Radio.Group;

class Movie extends Component {
    

    isSelected = (choice) => {
        return this.props.movie.selectedChoice === choice;
    }

    render() {
        
        let isVoteable = true;
        if(this.props.currentUsername){
            isVoteable = this.props.currentUsername === this.props.movie.publishedBy;
        }

        return (
            <div className="movie-content">
                <div className="movie-header">
                    <div className="movie-creator-info">
                        <Link className="creator-link" to={`/users/${this.props.movie.publishedBy}`}>
                            <Avatar className="movie-creator-avatar" 
                                style={{ backgroundColor: getAvatarColor(this.props.movie.publishedBy)}} >
                                {this.props.movie.publishedBy.slice(0,1).toUpperCase()}
                            </Avatar>
                            <span className="movie-creator-username">
                                Posted by: @{this.props.movie.publishedBy}
                            </span>
                            <span className="movie-creation-date">
                                Created at: {formatDateTime(this.props.movie.publishedDate)}
                            </span>
                        </Link>
                    </div>
                    <div className="movie-question">
                        {this.props.movie.title}
                    </div>
                    <div className="movie-question">
                        {this.props.movie.description}
                    </div>
                </div>

                <div className="movie-choices">
                    {
                        !isVoteable 
                        ? 
                        <RadioGroup 
                        className="movie-choice-radio-group" 
                        onChange={this.props.handleVoteChange} 
                        value={this.props.currentUserReaction}>
                        <Radio className="movie-choice-radio" key={'radio.likeId'} value={"LIKE"}>Like</Radio>
                        <Radio className="movie-choice-radio" key={'radio.hateId'} value={"HATE"}>Hate</Radio>
                        </RadioGroup>
                        :null
                    }

                </div>

                <div className="movie-footer">
                
                    {
                        !isVoteable 
                        ?
                        <Button className="vote-button" disabled={!this.props.currentVote} onClick={this.props.handleVoteSubmit}>Vote</Button>
                        :null

                    }
                    
                    <span className="total-votes">{this.props.movie.countOfLikes} <LikeOutlined/></span>
                    <span className="separator"></span>
                    <span className="total-votes">{this.props.movie.countOfHates} <DislikeOutlined/></span>
                    {
                        this.props.movie.currentUserReaction
                        ? <span className="separator">| You have voted {`${this.props.movie.currentUserReaction}`}.</span>
                        :<span className="separator"></span>
                    }

                    {
                        this.props.movie.currentUserReaction
                        ? <Button className="clear-button" disabled={!this.props.movie.currentUserReaction } onClick={this.props.handleClearVote}>Clear</Button>
                        :null
                    }
                </div>
            </div>
        );
    }
}

export default Movie;