import React, {Component} from 'react';
import './Movie.css';
import {Avatar, Card} from 'antd';
import {Link} from 'react-router-dom';
import {LikeOutlined, DislikeOutlined} from '@ant-design/icons';
import {getAvatarColor} from '../utils/Colors';
import {formatDateTime} from '../utils/Helpers';

import {Button} from 'antd';

class Movie extends Component {


    isSelected = (choice) => {
        return this.props.movie.selectedChoice === choice;
    }

    render() {


        let isVoteable = true;
        if (this.props.currentUsername) {
            isVoteable = this.props.currentUsername != this.props.movie.publishedBy;
        }

        return (
            <Card>
                <div className="movie-header">
                    <div className="movie-creator-info">
                        <Link className="creator-link" to={`/users/${this.props.movie.publishedBy}`}>
                            <Avatar className="movie-creator-avatar"
                                    style={{backgroundColor: getAvatarColor(this.props.movie.publishedBy)}}>
                                {this.props.movie.publishedBy.slice(0, 1).toUpperCase()}
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
                <div className="movie-footer">

                    {
                        isVoteable
                            ?
                            this.props.movie.currentUserReaction == 'LIKE'
                                ? <span className="total-votes">{this.props.movie.countOfLikes} <Button
                                    icon={<LikeOutlined style={{color: 'hotpink'}}/>} onClick={this.props.handleClearVote}/> </span>
                                : this.props.movie.currentUserReaction == 'HATE'
                                    ? <span className="total-votes">{this.props.movie.countOfLikes} <Button
                                        icon={<LikeOutlined/>} onClick={this.props.handleClearVote}/> </span>
                                    : <span className="total-votes">{this.props.movie.countOfLikes} <Button icon={<LikeOutlined/>}
                                        onClick={this.props.handleLike}/> </span>
                            : <span className="total-votes">{this.props.movie.countOfLikes}<LikeOutlined/></span>
                    }
                    <span className="separator"/>
                    {
                        isVoteable
                            ?
                            this.props.movie.currentUserReaction == 'HATE'
                                ? <span className="total-votes">{this.props.movie.countOfHates} <Button
                                    icon={<DislikeOutlined style={{color: 'hotpink'}}/>} onClick={this.props.handleClearVote}/> </span>
                                : this.props.movie.currentUserReaction == 'LIKE'
                                    ? <span className="total-votes">{this.props.movie.countOfHates} <Button
                                        icon={<DislikeOutlined/>} onClick={this.props.handleClearVote}/> </span>
                                    : <span className="total-votes">{this.props.movie.countOfHates} <Button
                                        icon={<DislikeOutlined/>} onClick={this.props.handleHate}/> </span>
                            : <span className="total-votes">{this.props.movie.countOfLikes} <DislikeOutlined/></span>
                    }
                </div>
            </Card>
        );
    }
}

export default Movie;