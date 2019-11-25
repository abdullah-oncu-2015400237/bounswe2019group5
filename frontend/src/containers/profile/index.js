import React, { Component } from "react";
import { Redirect } from "react-router";
import { bindActionCreators } from "redux";
import { connect } from "react-redux";
import Card from "react-bootstrap/Card";
import CssBaseline from "@material-ui/core/CssBaseline";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import {
  set_user_profile,
  set_other_user_profile,
} from "../../redux/action-creators/userInfo";
import styles from "./styles";
import { withStyles } from "@material-ui/core/styles";
import LangTab from "./langTab";
import AccountCircleIcon from "@material-ui/icons/AccountCircle";
import Paper from "@material-ui/core/Paper";
import Ratings from "./ratings";
import _ from "lodash"
import { get_essays } from '../../redux/action-creators/writinglist';



class Profile extends Component {
  state = { selfProfile: true };
  constructor(props) {
    super(props);
    console.log(props.userInfo);
  }
  componentDidMount() {
    if (
      this.props.match.params.user != this.props.userInfo.userProfile.username
    ) {
      console.log("other user profile")
      this.props.set_other_user_profile(
        this.props.userInfo.token,
        this.props.match.params.user
      );
      this.setState({ selfProfile: false });
      console.log("other profile");
    } else  {
      this.props.set_user_profile(this.props.userInfo.token);
      this.props.get_essays(this.props.userInfo.token);
      console.log("self profile");
    }
    
  }

  componentDidUpdate() {
    if (
      this.props.match.params.user != this.props.userInfo.userProfile.username
    ) {
      this.props.set_other_user_profile(
        this.props.userInfo.token,
        this.props.match.params.user
      );
      this.setState({ selfProfile: false });
      console.log("other profile");
    } else  {
      this.props.set_user_profile(this.props.userInfo.token);
      console.log("self profileuu");
      this.setState({ selfProfile: true });
    }
  }

  shouldComponentUpdate(nextProps, nextState) {
    return !_.isEqual(nextProps, this.props) || !_.isEqual(nextState, this.state)
  }

  render() {

    console.log(this.props.userInfo)
    const { classes } = this.props;

    if (this.props.userInfo.token == null) {
      return (
        <Redirect
          to={{
            pathname: "/login"
          }}
        />
      );
    }
    else if (this.props.userInfo.loading) {
      return (
        <div>
          <h1>LOADING</h1>
        </div>
      );
    } else {
      return (
        <Grid container component="main" className={classes.root}>
          <CssBaseline />
          <Grid xs={3} className={classes.paper}>
            <div className={classes.paper}>
              <Card
                border="warning"
                className="text-center"
                style={{ width: "18rem" }}
              >
                <Card.Header>
                  <AccountCircleIcon fontSize="large"> </AccountCircleIcon>
                </Card.Header>
                <Card.Body>
                  {this.state.selfProfile ? (
                    <Card.Title>
                      {" "}
                      {this.props.userInfo.userProfile.username}{" "}
                    </Card.Title>
                  ) : (
                    <Card.Title>
                      {" "}
                      {this.props.userInfo.otherUserProfile.username}{" "}
                    </Card.Title>
                  )}
                  <Card.Text>
                    {this.state.selfProfile ? (
                      <>
                      <Typography variant="h4" gutterBottom>
                        {this.props.userInfo.userProfile.first_name}{" "}
                        {this.props.userInfo.userProfile.last_name}
                      </Typography>
                      <Typography variant="h3" gutterBottom>
                      Overall rating: 
                      {this.props.userInfo.overallRating && (this.props.userInfo.overallRating[0] / this.props.userInfo.overallRating[1])} out of {this.props.userInfo.overallRating && this.props.userInfo.overallRating[1]} ratings.
                    </Typography>  
                    </>                    
                    ) : (
                      <>
                      <Typography variant="h4" gutterBottom>
                        {this.props.userInfo.otherUserProfile.first_name}{" "}
                        {this.props.userInfo.otherUserProfile.last_name}
                      </Typography>
                      <Typography variant="h3" gutterBottom>
                      Overall rating: 
                      {this.props.userInfo.overallRating[0] / this.props.userInfo.overallRating[1]} out of {this.props.userInfo.overallRating[1]} ratings.
                    </Typography>   
                    </>
                    )}
                  </Card.Text>
                </Card.Body>
              </Card>
            </div>
          </Grid>
          <Grid item component={Paper}>
            <div className={classes.paper}>
              {this.state.selfProfile && (
                <LangTab
                  userInfo= {this.props.userInfo}
                  attendedLang= {this.props.userInfo.userProfile.attended_languages}
                  writings = {this.props.writinglist && this.props.writinglist.writings}
                />
              )}
            </div>
            <Grid>
            <div className={classes.paper}>
            <Typography variant="h3" gutterBottom>
                     User ratings and comments:
            </Typography>  
              {this.state.selfProfile ? (
                <Ratings userProfile={this.props.userInfo.userProfile} />
              ) : (
                <Ratings userProfile={this.props.userInfo.otherUserProfile} />
              )}
              </div>
          </Grid>
          </Grid>
        </Grid>
      );
    }
  }
}
const mapStateToProps = ({ userInfo }) => {
  console.log('test', userInfo)
  return ({
  userInfo
})};

const mapDispatchToProps = dispatch =>
  bindActionCreators(
    {
      set_user_profile,
      set_other_user_profile,
      get_essays
    },
    dispatch
  );

export default withStyles(styles)(
  connect(mapStateToProps, mapDispatchToProps)(Profile)
);
